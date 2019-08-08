package no.priv.bang.modeling.modelstore.tests;

import static org.junit.Assert.*;
import static org.ops4j.pax.exam.CoreOptions.*;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Collection;
import java.util.UUID;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.options.MavenArtifactUrlReference;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

import no.priv.bang.modeling.modelstore.services.ModelContext;
import no.priv.bang.modeling.modelstore.services.Modelstore;
import no.priv.bang.modeling.modelstore.services.Propertyset;
import no.priv.bang.modeling.modelstore.services.Value;
import no.priv.bang.modeling.modelstore.services.ValueList;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class ModelstoreTest extends ModelstoreIntegrationtestBase {

    @Inject
    private Modelstore modelstoreService;

    @Configuration
    public Option[] config() {
        final MavenArtifactUrlReference karafUrl = maven().groupId("org.apache.karaf").artifactId("apache-karaf-minimal").type("zip").versionAsInProject();
        final MavenArtifactUrlReference authserviceFeatureRepo = maven().groupId("no.priv.bang.modeling.modelstore").artifactId("modelstore.backend").version("LATEST").type("xml").classifier("features");
        return options(
            karafDistributionConfiguration().frameworkUrl(karafUrl).unpackDirectory(new File("target/exam")).useDeployFolder(false).runEmbedded(true),
            configureConsole().ignoreLocalConsole().ignoreRemoteShell(),
            features(authserviceFeatureRepo, "modelstore.backend"),
            junitBundles());
    }

    @Test
    public void modelstoreIntegrationTest() {
        // Verify that the service could be injected
        assertNotNull(modelstoreService);

        // Actually use the service to create some propertysets
        ModelContext context = modelstoreService.getDefaultContext();
        UUID propertysetId = UUID.randomUUID();
        // Create a new propertyset
        Propertyset propertyset = context.findPropertyset(propertysetId);
        propertyset.setStringProperty("stringPropertyOfPi", "3.14");
        assertEquals(Double.valueOf(3.14), propertyset.getDoubleProperty("stringPropertyOfPi"));
        // Verify that using the same ID will result in the same propertyset
        Propertyset newPropertyset = context.findPropertyset(propertysetId);
        assertEquals(propertyset, newPropertyset);
    }

    @Test
    public void testList() {
        ModelContext context = modelstoreService.getDefaultContext();
        ValueList list = context.createList();
        assertEquals(0, list.size());
        list.add(true);
        assertTrue(list.get(0).asBoolean());
        list.add(3.14);
        assertEquals(3.14, list.get(1).asDouble(), 0.0);

        Propertyset referencedPropertyset = context.findPropertyset(UUID.randomUUID());
        list.add(referencedPropertyset);
        assertTrue(list.get(2).isReference());

        Propertyset containedPropertyset = context.createPropertyset();
        list.add(containedPropertyset);
        assertTrue(list.get(3).isComplexProperty());

        list.set(0, 42); // Overwrite boolean with long
        assertTrue(list.get(0).asBoolean()); // Non-null value gives true boolean
        assertEquals(42, list.get(0).asLong().longValue()); // The actual long value is still there

        ValueList containedlist = context.createList();
        containedlist.add(100);
        list.add(containedlist);
        assertEquals(1, list.get(4).asList().size());
        // Modifying original does not affect list element
        containedlist.add(2.5);
        assertEquals(2, containedlist.size());
        assertEquals(1, list.get(4).asList().size());

        assertEquals(5, list.size());
        list.remove(1);
        assertEquals(4, list.size());
    }

    @Test
    public void testEmbeddedAspects() {
        ModelContext context = modelstoreService.getDefaultContext();
        int numberOfEmbeddedAspects = 6; // Adjust when adding embedded aspects

        Collection<Propertyset> aspects = context.listAllAspects();
        assertEquals(numberOfEmbeddedAspects, aspects.size());
    }

    /**
     * Test saving a {@link ModelContext} to a {@link PipedOutputStream}
     * and then restoring the context from a {@link PipedInputStream}.
     *
     * Have to write to the pipe from a different thread than the reader,
     * or else it will deadlock.
     *
     * @throws IOException
     */
    @Test
    public void testPersistRestoreModelContextUsingPipedStreams() throws IOException {
        InputStream carsAndBicylesStream = getClass().getResourceAsStream("/json/cars_and_bicycles.json");
        final ModelContext context1 = modelstoreService.restoreContext(carsAndBicylesStream);
        assertEquals(6, context1.listAllAspects().size());

        PipedInputStream loadStream = new PipedInputStream();
        final OutputStream saveStream = new PipedOutputStream(loadStream);
        new Thread(new Runnable() {
                public void run() {
                    modelstoreService.persistContext(saveStream, context1);
                }
            }).start();

        ModelContext context2 = modelstoreService.restoreContext(loadStream);

        compareAllPropertysets(context1, context2);
    }

    /**
     * Iterate over all of the {@link Propertyset} instances of a
     * {@link Modelstore} and compare them to the propertyesets
     * of a different Modelstore and assert that they match.
     *
     * @param context the {@link ModelContext} to iterate over
     * @param context2 the {@link ModelContext} to compare with
     */
    public static void compareAllPropertysets(ModelContext context, ModelContext context2) {
        for (Propertyset propertyset : context.listAllPropertysets()) {
            Propertyset parsedPropertyset = context2.findPropertyset(propertyset.getId());
            for (String propertyname : propertyset.getPropertynames()) {
                Value originalValue = propertyset.getProperty(propertyname);
                Value parsedValue = parsedPropertyset.getProperty(propertyname);
                assertEquals(originalValue, parsedValue);
            }
        }
    }

}
