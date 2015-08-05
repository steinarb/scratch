package no.priv.bang.modeling.modelstore.impl;

import static no.priv.bang.modeling.modelstore.impl.Aspects.generalObjectAspectId;
import static no.priv.bang.modeling.modelstore.impl.Aspects.modelAspectId;
import static no.priv.bang.modeling.modelstore.impl.Propertysets.findWrappedPropertyset;
import static no.priv.bang.modeling.modelstore.testutils.TestUtils.compareAllPropertysets;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.UUID;

import no.priv.bang.modeling.modelstore.ModelContext;
import no.priv.bang.modeling.modelstore.Propertyset;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.fasterxml.jackson.core.JsonFactory;

/**
 * Unit tests for class {@link ModelContextImpl}.
 *
 * @author Steinar Bang
 *
 */
public class ModelContextImplTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    /**
     * Unit test for {@link ModelContextImpl#merge(ModelContext} when the
     * two modelcontexts being merged has no overlap.
     *
     * Note: this differs from {@link ModelContextTest#testMergeNoOverlapBetweenContexts()}
     * in that the {@link ModelContext} under test has no metadata about last modified
     * time for each propertyset, so that the merged-in propertyset will always
     * "win" over the propertyset that is merged into.
     *
     * @throws IOException
     */
    @Test
    public void testMergeNoOverlapBetweenContexts() throws IOException {
        ModelContext context = new ModelContextImpl();
        buildPropertysetA(context, UUID.randomUUID());
        assertEquals("Expected context to contain 1 propertyset", 1, context.listAllPropertysets().size());

        ModelContext otherContext = new ModelContextImpl();
        UUID bId = UUID.randomUUID();
        buildPropertysetB(otherContext, bId);
        assertEquals("Expected otherContext to contain 1 propertyset", 1, otherContext.listAllPropertysets().size());

        context.merge(otherContext);
        assertEquals("Expected context to contain 2 propertysets", 2, context.listAllPropertysets().size());
        // Verify that the copied "B" is the same as the original B
        // TODO decide if PropertysetRecordingSaveTime.equals() should include the context in comparison, for now: get the inner PropertysetImpl instances and compare them instead
        Propertyset originalB = findWrappedPropertyset(otherContext.findPropertyset(bId));
        Propertyset mergedB = findWrappedPropertyset(context.findPropertyset(bId));
        assertEquals(originalB, mergedB);

        // Save and restore the merged context and verify that the restored context is the same as the merged context
        File propertysetsFile = folder.newFile("mergedcontext.json");
        OutputStream saveStream = Files.newOutputStream(propertysetsFile.toPath());
        JsonFactory factory = new JsonFactory();
        JsonPropertysetPersister persister = new JsonPropertysetPersister(factory);
        persister.persist(saveStream, context);
        InputStream loadStream = Files.newInputStream(propertysetsFile.toPath());
        ModelContext restoredContext = new ModelContextImpl();
        persister.restore(loadStream, restoredContext);
        compareAllPropertysets(context, restoredContext);
    }

    /**
     * Unit test for {@link ModelContextImpl#merge(ModelContext)} when the
     * two modelcontexts being merged has overlap: the and b objects exists
     * in both contexts and the newest values are kept. a is newest in the
     * otherContext and b is newest in context.
     *
     * Note: this differs from {@link ModelContextTest#testMergeWithOverlapBetweenContexts()}
     * in that the {@link ModelContext} under test has no metadata about last modified
     * time for each propertyset, so that the merged-in propertyset will always
     * "win" over the propertyset that is merged into.

     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    public void testMergeWithOverlapBetweenContexts() throws IOException, InterruptedException {
        ModelContext context = new ModelContextImpl();
        UUID aId = UUID.randomUUID();
        buildPropertysetA(context, aId);
        assertEquals("Expected context to contain 1 propertyset", 1, context.listAllPropertysets().size());

        // Wait a few milliseconds to get a different time stamp
        Thread.sleep(10);
        ModelContext otherContext = new ModelContextImpl();
        UUID bId = UUID.randomUUID();
        buildPropertysetA(otherContext, aId);
        otherContext.findPropertyset(aId).setLongProperty("value", 42);
        Propertyset generalObjectAspect = otherContext.findPropertyset(generalObjectAspectId);
        otherContext.findPropertyset(aId).addAspect(generalObjectAspect);
        buildPropertysetB(otherContext, bId);
        otherContext.findPropertyset(bId).addAspect(generalObjectAspect);
        assertEquals("Expected otherContext to contain 2 propertysets", 2, otherContext.listAllPropertysets().size());

        // Wait a few milliseconds to get a different time stamp, then create "b" in the
        // in the first context, with a slightly newer time stamp, meaning it should be kept
        Thread.sleep(10);
        buildPropertysetB(context, bId);
        context.findPropertyset(bId).setLongProperty("value", 4); // Change the value, should be kept after merge
        Propertyset modelAspect = context.findPropertyset(modelAspectId);
        context.findPropertyset(bId).addAspect(modelAspect);
        assertEquals("Expected context to contain 2 propertysets", 2, context.listAllPropertysets().size());

        context.merge(otherContext);

        // Verify the merge results
        assertEquals("Expected context to contain 2 propertysets", 2, context.listAllPropertysets().size());
        // Check that the "value" in "b" and in "a" both come from otherContext, since there is no lastmodifiedtime recorded
        assertEquals(42, context.findPropertyset(aId).getLongProperty("value").longValue());
        assertEquals(1.2, context.findPropertyset(bId).getDoubleProperty("value").doubleValue(), 0.0);
        // Check that "a" has aspect "general object"
        assertEquals(1, context.findPropertyset(aId).getAspects().size());
        assertEquals(generalObjectAspectId, context.findPropertyset(aId).getAspects().get(0).asReference().getId());
        // Check that "b" has two aspects: first "model" (oldest) and then "general object" (newest)
        assertEquals(2, context.findPropertyset(bId).getAspects().size());
        assertEquals(modelAspectId, context.findPropertyset(bId).getAspects().get(0).asReference().getId());
        assertEquals(generalObjectAspectId, context.findPropertyset(bId).getAspects().get(1).asReference().getId());

        // Save and restore the merged context and verify that the restored context is the same as the merged context
        File propertysetsFile = folder.newFile("mergedcontext.json");
        OutputStream saveStream = Files.newOutputStream(propertysetsFile.toPath());
        JsonFactory factory = new JsonFactory();
        JsonPropertysetPersister persister = new JsonPropertysetPersister(factory);
        persister.persist(saveStream, context);
        InputStream loadStream = Files.newInputStream(propertysetsFile.toPath());
        ModelContext restoredContext = new ModelContextImpl();
        persister.restore(loadStream, restoredContext);
        compareAllPropertysets(context, restoredContext);
    }

    /**
     * Unit test for {@link ModelContextImpl#hashCode()}.
     */
    @Test
    public void testHashCode() {
        ModelContext context = new ModelContextImpl();
        assertEquals(216866173, context.hashCode());
        addPropertysetsToContext(context);
        assertEquals(-1809752513, context.hashCode());
    }

    /**
     * Unit test for {@link ModelContextImpl#equals()}.
     */
    @Test
    public void testEquals() {
        ModelContext context = new ModelContextImpl();
        addPropertysetsToContext(context);
        assertTrue(context.equals(context));
        assertFalse(context.equals(null));
        assertFalse(context.equals(new PropertysetImpl()));
        ModelContext identicalContext = new ModelContextImpl();
        addPropertysetsToContext(identicalContext);
        assertTrue(context.equals(identicalContext));
    }

    /**
     * Unit test for {@link ModelContextImpl#toString()}.
     */
    @Test
    public void testToString() {
        ModelContext context = new ModelContextImpl();
        addPropertysetsToContext(context);
        assertThat(context.toString(), startsWith("ModelContextImpl "));
    }

    private void addPropertysetsToContext(ModelContext context) {
        UUID aId = UUID.fromString("481f1eda-2acc-4d8e-8618-c29534408e2e");
        buildPropertysetA(context, aId);
        UUID bId = UUID.fromString("018bbff7-369a-4dfd-8e02-92fcc581819d");
        buildPropertysetB(context, bId);
    }

    private void buildPropertysetA(ModelContext context, UUID aId) {
        Propertyset propertyset1 = context.findPropertyset(aId);
        propertyset1.setStringProperty("name", "a");
        propertyset1.setDoubleProperty("value", 2.1);
    }

    private void buildPropertysetB(ModelContext context, UUID bId) {
        Propertyset propertyset1 = context.findPropertyset(bId);
        propertyset1.setStringProperty("name", "b");
        propertyset1.setDoubleProperty("value", 1.2);
    }

}
