package no.priv.bang.modeling.modelstore.backend;

import static no.priv.bang.modeling.modelstore.testutils.TestUtils.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import no.priv.bang.modeling.modelstore.mocks.MockOutputStreamThatThrowsIOExceptionOnEverything;
import no.priv.bang.modeling.modelstore.services.ErrorBean;
import no.priv.bang.modeling.modelstore.services.ModelContext;
import no.priv.bang.modeling.modelstore.services.Modelstore;
import no.priv.bang.modeling.modelstore.services.Propertyset;
import no.priv.bang.modeling.modelstore.services.ValueList;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;

/**
 * Unit tests for {@link JsonPropertysetPersister}.
 *
 * @author Steinar Bang
 *
 */
public class JsonPropertysetPersisterTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * Parse a file with propertysets and aspects.
     * @throws URISyntaxException
     * @throws IOException
     * @throws JsonParseException
     */
    @Test
    public void testParseComplexFile() throws URISyntaxException, JsonParseException, IOException {
        Modelstore modelstore = new ModelstoreProvider();
        ModelContext context = modelstore.getDefaultContext();
        File carsAndBicycles = getResourceAsFile("/json/cars_and_bicycles.json");

        JsonFactory jsonFactory = new JsonFactory();
        JsonPropertysetPersister persister = new JsonPropertysetPersister(jsonFactory);

        // Read the contents of the file into memory
        persister.restore(carsAndBicycles, context);

        assertEquals(9, context.listAllAspects().size());
        assertEquals(8, context.listAllPropertysets().size());
    }

    /**
     * Test parsing with the id field not first.
     * @throws URISyntaxException
     * @throws IOException
     * @throws JsonParseException
     */
    @Test
    public void testParseIdNotFirst() throws URISyntaxException, JsonParseException, IOException {
        Modelstore modelstore = new ModelstoreProvider();
        ModelContext context = modelstore.getDefaultContext();
        File carsAndBicyclesIdNotFirst = getResourceAsFile("/json/cars_and_bicycles_id_not_first.json");

        JsonFactory jsonFactory = new JsonFactory();
        JsonPropertysetPersister persister = new JsonPropertysetPersister(jsonFactory);

        // Read the contents of the file into memory
        persister.restore(carsAndBicyclesIdNotFirst, context);

        assertEquals(9, context.listAllAspects().size());
        assertEquals(8, context.listAllPropertysets().size());

        // Verify that the results are identical to the ones with the id first
        Modelstore modelstore2 = new ModelstoreProvider();
        ModelContext context2 = modelstore2.getDefaultContext();
        File carsAndBicycles = getResourceAsFile("/json/cars_and_bicycles.json");
        persister.restore(carsAndBicycles, context2);
        compareAllPropertysets(context, context2);
    }

    /**
     * Test that boolean fields can be parsed.
     * @throws URISyntaxException
     * @throws IOException
     * @throws JsonParseException
     */
    @Test
    public void parseBooleanProperties() throws URISyntaxException, JsonParseException, IOException {
        Modelstore modelstore = new ModelstoreProvider();
        ModelContext context = modelstore.getDefaultContext();
        File withBoolean = getResourceAsFile("/json/with_boolean.json");
        JsonFactory jsonFactory = new JsonFactory();
        JsonPropertysetPersister persister = new JsonPropertysetPersister(jsonFactory);

        // Read the contents of the file into memory
        persister.restore(withBoolean, context);

        // Check the parsed values
        Propertyset propertyset = context.listAllPropertysets().iterator().next();
        assertFalse(propertyset.getBooleanProperty("untrue"));
        assertTrue(propertyset.getBooleanProperty("unfalse"));

        // Output the boolean values to a different file
        File propertysetsFile = folder.newFile("boolean.json");
        persister.persist(propertysetsFile, context);

        // Read the file back in and compare it with the original
        Modelstore modelstore2 = new ModelstoreProvider();
        ModelContext context2 = modelstore2.getDefaultContext();
        persister.restore(propertysetsFile, context2);

        // Verify that the results of the second parse are identical to the first
        compareAllPropertysets(context, context2);
    }

    /**
     * Test that it works to parse JSON where the file doesn't start with an
     * array but with an object.
     *
     * This format is close to what the actual persist format will look like.
     *
     * @throws URISyntaxException
     * @throws IOException
     * @throws JsonParseException
     */
    @Test
    public void parseObjectOnTop() throws URISyntaxException, JsonParseException, IOException {
        Modelstore modelstore = new ModelstoreProvider();
        ModelContext context = modelstore.getDefaultContext();
        File withBoolean = getResourceAsFile("/json/object_on_top.json");
        JsonFactory jsonFactory = new JsonFactory();
        JsonPropertysetPersister persister = new JsonPropertysetPersister(jsonFactory);

        // Read the contents of the file into memory
        persister.restore(withBoolean, context);

        // Check the parsed values
        assertEquals(2, context.listAllPropertysets().size());

        // Output the two propertysets to a different file
        File twoObjectsFile = folder.newFile("two_objects.json");
        persister.persist(twoObjectsFile, context);

        // Read the file back in and compare it with the original
        Modelstore modelstore2 = new ModelstoreProvider();
        ModelContext context2 = modelstore2.getDefaultContext();
        persister.restore(twoObjectsFile, context2);

        // Verify that the results of the second parse are identical to the first
        compareAllPropertysets(context, context2);
    }

    /**
     * Test parsing propertyset with a list property.
     *
     * @throws URISyntaxException
     * @throws IOException
     * @throws JsonParseException
     */
    @Test
    public void parseListProperty() throws URISyntaxException, JsonParseException, IOException {
        Modelstore modelstore = new ModelstoreProvider();
        ModelContext context = modelstore.getDefaultContext();
        File withListProperty = getResourceAsFile("/json/with_list_property.json");
        JsonFactory jsonFactory = new JsonFactory();
        JsonPropertysetPersister persister = new JsonPropertysetPersister(jsonFactory);

        // Read the contents of the file into memory
        persister.restore(withListProperty, context);

        // Check the parsed values
        assertEquals(1, context.listAllPropertysets().size());
        Propertyset propertyset = context.listAllPropertysets().iterator().next();
        assertEquals(7, propertyset.getListProperty("listofthings").size());

        // Output the two propertysets to a different file
        File saveRestoreFile = folder.newFile("list_property.json");
        persister.persist(saveRestoreFile, context);

        // Read the file back in and compare it with the original
        Modelstore modelstore2 = new ModelstoreProvider();
        ModelContext context2 = modelstore2.getDefaultContext();
        persister.restore(saveRestoreFile, context2);

        // Verify that the results of the second parse are identical to the first
        assertEquals(1, context2.listAllPropertysets().size());
        Propertyset propertyset2 = context2.listAllPropertysets().iterator().next();
        assertEquals(7, propertyset2.getListProperty("listofthings").size());
        compareAllPropertysets(context, context2);
    }

    /**
     * Corner case unit test for {@link JsonPropertysetPersister#persist(File, ModelContext)}
     * try output into a null {@link File}.
     * @throws IOException
     */
    @Test
    public void testPersistNullFile() throws IOException {
        thrown.expect(NullPointerException.class);
        Modelstore modelstore = new ModelstoreProvider();
        ModelContext context = modelstore.createContext();
        JsonFactory jsonFactory = new JsonFactory();
        JsonPropertysetPersister persister = new JsonPropertysetPersister(jsonFactory);

        // Read the contents of the file into memory
        persister.persist((File)null, context);
    }

    /**
     * Corner case unit test for {@link JsonPropertysetPersister#persist(File, ModelContext)}
     * try output into a null {@link File} in a directory that doesn't exist.
     * @throws IOException
     */
    @Test
    public void testPersistFileInNonexistingDirectory() throws IOException {
        thrown.expect(FileNotFoundException.class);
        Modelstore modelstore = new ModelstoreProvider();
        ModelContext context = modelstore.createContext();
        JsonFactory jsonFactory = new JsonFactory();
        JsonPropertysetPersister persister = new JsonPropertysetPersister(jsonFactory);

        // A File in a non-existing directory
        File noSuchDirectory = new File("/nosuchdirectory/file.json");

        // Read the contents of the file into memory
        persister.persist(noSuchDirectory, context);
    }

    /**
     * Corner case unit test for {@link JsonPropertysetPersister#persist(java.io.OutputStream, ModelContext)}
     * try output into a null {@link OutputStream}.
     */
    @Test
    public void testPersistNullStream() {
        Modelstore modelstore = new ModelstoreProvider();
        ModelContext context = modelstore.createContext();
        JsonFactory jsonFactory = new JsonFactory();
        JsonPropertysetPersister persister = new JsonPropertysetPersister(jsonFactory);

        // Read the contents of the file into memory
        persister.persist((OutputStream)null, context);

        // Check that the exception has been logged
        List<ErrorBean> errors = modelstore.getErrors();
        assertEquals(2, errors.size());
        assertEquals("Caught exception outputting stream", errors.get(0).getMessage());
        assertNull(errors.get(0).getFileOrStream());
        assertEquals(NullPointerException.class, errors.get(0).getException().getClass());
        assertEquals("Caught exception closing output stream", errors.get(1).getMessage());
        assertNull(errors.get(1).getFileOrStream());
        assertEquals(NullPointerException.class, errors.get(1).getException().getClass());
    }

    /**
     * Corner case unit test for {@link JsonPropertysetPersister#persist(java.io.OutputStream, ModelContext)}
     * try output into a {@link MockOutputStreamThatThrowsIOExceptionOnEverything}.
     */
    @Test
    public void testPersistIntoStreamThrowingExceptions() {
        Modelstore modelstore = new ModelstoreProvider();
        ModelContext context = modelstore.createContext();
        JsonFactory jsonFactory = new JsonFactory();
        JsonPropertysetPersister persister = new JsonPropertysetPersister(jsonFactory);

        // Read the contents of the file into memory
        persister.persist(new MockOutputStreamThatThrowsIOExceptionOnEverything(), context);
        Date now = new Date(); // Will be within 1ms of the stream close error

        // Check that the exception has been logged
        List<ErrorBean> errors = modelstore.getErrors();
        assertEquals(2, errors.size());
        assertEquals(now, errors.get(0).getDate());
        assertEquals("Caught exception outputting stream", errors.get(0).getMessage());
        assertThat(errors.get(0).getFileOrStream(), startsWith("no.priv.bang.modeling.modelstore.mocks.MockOutputStreamThatThrowsIOExceptionOnEverything"));
        assertEquals(IOException.class, errors.get(0).getException().getClass());
        assertEquals(now, errors.get(1).getDate());
        assertEquals("Caught exception closing output stream", errors.get(1).getMessage());
        assertThat(errors.get(1).getFileOrStream(), startsWith("no.priv.bang.modeling.modelstore.mocks.MockOutputStreamThatThrowsIOExceptionOnEverything"));
        assertEquals(IOException.class, errors.get(1).getException().getClass());
    }

    /**
     * Corner case unit test for {@link JsonPropertysetPersister#restore(File, ModelContext)}
     * try parsing a null {@link InputStream}.
     * @throws IOException
     * @throws JsonParseException
     */
    @Test
    public void testRestoreNullFile() throws JsonParseException, IOException {
        Modelstore modelstore = new ModelstoreProvider();
        ModelContext context = modelstore.createContext();
        JsonFactory jsonFactory = new JsonFactory();
        JsonPropertysetPersister persister = new JsonPropertysetPersister(jsonFactory);

        // Read the contents of the file into memory
        Collection<Propertyset> beforeContents = context.listAllPropertysets();
        persister.restore((File)null, context);

        // Check that no exception has been logged
        List<ErrorBean> errors = modelstore.getErrors();
        assertEquals(0, errors.size());

        // Verify that the contents of the context hasn't changed
        assertEquals(beforeContents, context.listAllPropertysets());
    }

    /**
     * Corner case unit test for {@link JsonPropertysetPersister#restore(File, ModelContext)}
     * try parsing a file that doesn't contain JSON.
     *
     * @throws URISyntaxException
     * @throws IOException
     * @throws JsonParseException
     */
    @Test
    public void testRestoreFileNotJson() throws URISyntaxException, JsonParseException, IOException {
        thrown.expect(JsonParseException.class);
        Modelstore modelstore = new ModelstoreProvider();
        ModelContext context = modelstore.createContext();
        JsonFactory jsonFactory = new JsonFactory();
        JsonPropertysetPersister persister = new JsonPropertysetPersister(jsonFactory);

        // A file that isn't JSON
        File notJsonFile = getResourceAsFile("/json/not_json.json");

        // Read the contents of the file into memory
        persister.restore(notJsonFile, context);
    }

    /**
     * Corner case unit test for {@link JsonPropertysetPersister#restore(java.io.InputStream, ModelContext)}
     * try parsing a null {@link InputStream}.
     */
    @Test
    public void testRestoreNullStream() {
        Modelstore modelstore = new ModelstoreProvider();
        ModelContext context = modelstore.createContext();
        JsonFactory jsonFactory = new JsonFactory();
        JsonPropertysetPersister persister = new JsonPropertysetPersister(jsonFactory);

        // Read the contents of the file into memory
        persister.restore((InputStream)null, context);

        // Check that the exception has been logged
        List<ErrorBean> errors = modelstore.getErrors();
        assertEquals(1, errors.size());
        assertEquals("Caught exception trying to close a JSON file", errors.get(0).getMessage());
        assertNull(errors.get(0).getFileOrStream());
        assertEquals(NullPointerException.class, errors.get(0).getException().getClass());
    }

    /**
     * Corner case unit test for {@link JsonPropertysetPersister#restore(java.io.InputStream, ModelContext)}
     * try parsing a file that doesn't contain JSON.
     *
     * @throws URISyntaxException
     * @throws IOException
     */
    @Test
    public void testRestoreStreamNotJson() throws URISyntaxException, IOException {
        Modelstore modelstore = new ModelstoreProvider();
        ModelContext context = modelstore.createContext();
        JsonFactory jsonFactory = new JsonFactory();
        JsonPropertysetPersister persister = new JsonPropertysetPersister(jsonFactory);

        // A file that isn't JSON
        File notJsonFile = getResourceAsFile("/json/not_json.json");
        InputStream notJsonStream = Files.newInputStream(notJsonFile.toPath());

        // Read the contents of the file into memory
        persister.restore(notJsonStream, context);

        // Check that the exception has been logged
        List<ErrorBean> errors = modelstore.getErrors();
        assertEquals(1, errors.size());
        assertNotNull(errors.get(0).getDate());
        assertEquals("Caught exception parsing a JSON file", errors.get(0).getMessage());
        assertThat(errors.get(0).getFileOrStream(), startsWith("sun.nio.ch.ChannelInputStream"));
        assertEquals(JsonParseException.class, errors.get(0).getException().getClass());
    }

    /**
     * Unit test for {@link JsonPropertysetPersister#restore(java.io.InputStream, ModelContext)}
     *
     * @throws URISyntaxException
     * @throws IOException
     */
    @Test
    public void testRestoreFromStream() throws URISyntaxException, IOException {
        Modelstore modelstore = new ModelstoreProvider();
        ModelContext context = modelstore.createContext();
        JsonFactory jsonFactory = new JsonFactory();
        JsonPropertysetPersister persister = new JsonPropertysetPersister(jsonFactory);

        // A file that isn't JSON
        File jsonFile = getResourceAsFile("/json/cars_and_bicycles.json");
        InputStream jsonStream = Files.newInputStream(jsonFile.toPath());

        // Read the contents of the file into memory
        persister.restore(jsonStream, context);

        // Check that no exceptions has been logged
        List<ErrorBean> errors = modelstore.getErrors();
        assertEquals(0, errors.size());

        // Test that the expected number of propertysets and aspects are in place
        assertEquals(9, context.listAllAspects().size()); // Both built-in and parsed
        assertEquals(9, context.listAllPropertysets().size()); // Only parsed aspects and propertysets + metadata
    }

    /**
     * Corner case unit test for {@link JsonPropertysetPersister#restore(java.io.InputStream, ModelContext)}
     * Test parsing an empty array.
     *
     * @throws URISyntaxException
     * @throws IOException
     */
    @Test
    public void testRestoreEmptyArrayFromStream() throws URISyntaxException, IOException {
        Modelstore modelstore = new ModelstoreProvider();
        ModelContext context = modelstore.createContext();
        JsonFactory jsonFactory = new JsonFactory();
        JsonPropertysetPersister persister = new JsonPropertysetPersister(jsonFactory);

        // A file that isn't JSON
        File jsonFile = getResourceAsFile("/json/empty_array.json");
        InputStream jsonStream = Files.newInputStream(jsonFile.toPath());

        // Read the contents of the file into memory
        persister.restore(jsonStream, context);

        // Check that no exceptions has been logged
        List<ErrorBean> errors = modelstore.getErrors();
        assertEquals(0, errors.size());

        // Test that the expected number of propertysets and aspects are in place
        assertEquals(6, context.listAllAspects().size()); // Only built-in aspects
        assertEquals(1, context.listAllPropertysets().size()); // Only the metadata object
    }

    /**
     * Corner case unit test for {@link JsonPropertysetPersister#restore(java.io.InputStream, ModelContext)}
     * Test parsing a JSON file with an object on the top level instead of an array.
     *
     * @throws URISyntaxException
     * @throws IOException
     */
    @Test
    public void testRestoreObjectOnTopLevelFromStream() throws URISyntaxException, IOException {
        Modelstore modelstore = new ModelstoreProvider();
        ModelContext context = modelstore.createContext();
        JsonFactory jsonFactory = new JsonFactory();
        JsonPropertysetPersister persister = new JsonPropertysetPersister(jsonFactory);

        // A file that isn't JSON
        File jsonFile = getResourceAsFile("/json/object_on_top.json");
        InputStream jsonStream = Files.newInputStream(jsonFile.toPath());

        // Read the contents of the file into memory
        persister.restore(jsonStream, context);

        // Check that no exceptions has been logged
        List<ErrorBean> errors = modelstore.getErrors();
        assertEquals(0, errors.size());

        // Test that the expected number of propertysets and aspects are in place
        assertEquals(6, context.listAllAspects().size()); // Only built-in aspects
        assertEquals(3, context.listAllPropertysets().size()); // The metadata object and the two objects in "propertysets"
    }

    /**
     * Not actually a unit test.  Just a convenient way to create a new
     * {@link Propertyset} with an unique id and write it to the console.
     *
     * @throws IOException
     */
    @Ignore
    @Test
    public void generatePropertysetWithId() throws IOException {
        Modelstore modelstore = new ModelstoreProvider();
        ModelContext context = modelstore.getDefaultContext();
        context.findPropertyset(UUID.randomUUID());
        context.findPropertyset(UUID.randomUUID());
        JsonFactory jsonFactory = new JsonFactory();
        JsonPropertysetPersister persister = new JsonPropertysetPersister(jsonFactory);
        File propertysetsFile = folder.newFile("propertyset.json");
        persister.persist(propertysetsFile, context);
        String contents = new String(Files.readAllBytes(propertysetsFile.toPath()));
        System.out.println(contents);
    }

    /**
     * Not actually a unit test.  Generates a simple model
     * with 10 interconnected nodes and outputs JSON for the model.
     *
     * @throws IOException
     */
    @Ignore
    @Test
    public void generateSimpleModel() throws IOException {
        Modelstore modelstore = new ModelstoreProvider();
        ModelContext context = modelstore.createContext();
        Propertyset modelAspect = context.findPropertyset(modelstore.getModelAspectId());
        Propertyset generalObjectAspect = context.findPropertyset(modelstore.getGeneralObjectAspectId());
        Propertyset relationshipAspect = context.findPropertyset(modelstore.getRelationshipAspectId());

        Propertyset model = context.findPropertyset(UUID.randomUUID());
        model.addAspect(modelAspect);

        ValueList nodes = new ValueArrayList();
        for (int i = 0; i < 10; i++) {
            Propertyset node = context.findPropertyset(UUID.randomUUID());
            node.addAspect(generalObjectAspect);
            String nodeName = "Node " + i;
            node.setStringProperty("name", nodeName);
            nodes.add(node);
        }

        ValueList connections = new ValueArrayList();
        for (int i = 0; i < 10 - 1; i++) {
            Propertyset connection = context.findPropertyset(UUID.randomUUID());
            connection.addAspect(relationshipAspect);

            connection.setReferenceProperty("origin", nodes.get(i).asReference());
            connection.setReferenceProperty("target", nodes.get(i + 1).asReference());
            connections.add(connection);
        }

        model.setListProperty("objects", nodes);
        model.setListProperty("relationships", connections);

        JsonFactory jsonFactory = new JsonFactory();
        JsonPropertysetPersister persister = new JsonPropertysetPersister(jsonFactory);
        File propertysetsFile = folder.newFile("testmodel.json");
        persister.persist(propertysetsFile, context);
        String contents = new String(Files.readAllBytes(propertysetsFile.toPath()));
        System.out.println(contents);
    }
}
