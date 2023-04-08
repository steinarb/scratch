package no.priv.bang.modeling.modelstore.backend;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.fasterxml.jackson.core.JsonFactory;

import no.priv.bang.modeling.modelstore.services.DateFactory;
import no.priv.bang.modeling.modelstore.services.ModelContext;
import no.priv.bang.modeling.modelstore.services.Modelstore;
import no.priv.bang.modeling.modelstore.services.Propertyset;
import no.priv.bang.modeling.modelstore.services.ValueList;
import no.priv.bang.modeling.modelstore.value.ValueCreatorProvider;

/**
 * Unit tests for {@link ModelContextRecordingMetadata} which
 * is an implementation of {@link ModelContext} that records
 * changes to other propertysets in a propertyset saved with
 * the rest.
 *
 */
class ModelContextRecordingMetadataTest {
    private JsonFactory jsonFactory;
    private JsonPropertysetPersister persister;
    private ValueCreatorProvider valueCreator;
    private DateFactory dateFactory;
    private ModelContextImpl nonMetadataRecordingContext;
    private ModelContextRecordingMetadata context;
    private Propertyset vehicleAspect;
    private Propertyset carAspect;

    @TempDir
    File folder;

    @BeforeEach
    void setup() {
        valueCreator = new ValueCreatorProvider();
        jsonFactory = new JsonFactory();
        persister = new JsonPropertysetPersister(jsonFactory, valueCreator);
        var modelstore = new ModelstoreProvider();
        modelstore.setValueCreator(valueCreator);
        modelstore.activate();
        nonMetadataRecordingContext = (ModelContextImpl) modelstore.getDefaultContext();
        persister.restore(getClass().getResourceAsStream("/json/cars_and_bicycles_id_not_first.json"), nonMetadataRecordingContext);
        dateFactory = new DateFactory() {

                @Override
                public Date now() {
                    return new Date();
                }
            };
        context = new ModelContextRecordingMetadata(nonMetadataRecordingContext, dateFactory, valueCreator);
        vehicleAspect = context.findPropertyset(UUID.fromString("42810a4d-b757-430a-a839-75737a027c59"));
        carAspect = context.findPropertyset(UUID.fromString("7d4a452a-a502-4333-bd0b-f42dc4d1bc82"));
    }

    /**
     * Test getting a {@link Propertyset} with a {@link PropertysetRecordingSaveTime}
     * wrapper and comparing the propertyset and propertyset-with-a-wrapper.
     */
    @Test
    void testCreateModelContextRecordingMetadata() {
        UUID outbackId = UUID.fromString("5b66f36b-4de8-4099-9044-ad9fcc6dc4d1");
        Propertyset unwrappedOutback = nonMetadataRecordingContext.findPropertyset(outbackId);
        Propertyset wrappedOutback = context.findPropertyset(outbackId);
        assertEquals("Subaru", unwrappedOutback.getStringProperty("manufacturer"));
        assertEquals("Subaru", wrappedOutback.getStringProperty("manufacturer"));
        assertEquals(wrappedOutback, unwrappedOutback, "Expected wrapped and unwrapped propertyset to test equal");
    }

    /**
     * Test initial propertyset modified time values, modify propertysets,
     * save and restore the propertyset values, and compare the modified
     * time values of the restored propertysets with the times in memory.
     * @throws IOException
     */
    @Test
    void testInitialPropertysetModifiedTimeValuesModifyPropertysetsSaveAndRestor() throws IOException {
        // Verify that initial modification times are all null (no persisted modification times)
        UUID outbackId = UUID.fromString("5b66f36b-4de8-4099-9044-ad9fcc6dc4d1");
        Propertyset outback1 = context.findPropertyset(outbackId);
        assertNull(context.getLastmodifieddate(outback1));
        UUID tacomaId = UUID.fromString("7fd99b6c-603c-4965-8fc3-00e2d2ac2202");
        Propertyset tacoma1 = context.findPropertyset(tacomaId);
        assertNull(context.getLastmodifieddate(tacoma1));
        UUID volvoId = UUID.fromString("31c4b2e9-c66f-40d6-9b5e-fe8a26947460");
        Propertyset volvo1 = context.findPropertyset(volvoId);
        assertNull(context.getLastmodifieddate(volvo1));
        UUID nakamuraId = UUID.fromString("384d417e-a38a-4b5d-ad39-23148545a1b8");
        Propertyset nakamura1 = context.findPropertyset(nakamuraId);
        assertNull(context.getLastmodifieddate(nakamura1));
        UUID ferrariId = UUID.fromString("bb87fff5-9582-4ab1-9890-942fc35d44b2");
        Propertyset ferrari1 = context.findPropertyset(ferrariId);
        assertNull(context.getLastmodifieddate(ferrari1));

        // Change all of the propertysets, and verify that lastmodifiedtime is non-null
        Propertyset outback2 = context.findPropertyset(outbackId);
        outback2.setDoubleProperty("engineSize", 2.6);
        assertNotNull(context.getLastmodifieddate(outback2));
        Propertyset tacoma2 = context.findPropertyset(tacomaId);
        tacoma2.setStringProperty("manufacturer", "HODE");
        assertNotNull(context.getLastmodifieddate(tacoma2));
        Propertyset volvo2 = context.findPropertyset(volvoId);
        volvo2.setDoubleProperty("enginesize", Double.valueOf(1.9));
        assertNotNull(context.getLastmodifieddate(volvo2));
        Propertyset nakamura2 = context.findPropertyset(nakamuraId);
        nakamura2.setBooleanProperty("stolen", true);
        assertNotNull(context.getLastmodifieddate(nakamura2));
        Propertyset ferrari2 = context.findPropertyset(ferrariId);
        ferrari2.setLongProperty("carnumber", 42);
        assertNotNull(context.getLastmodifieddate(ferrari2));

        // Save to and restore from a file
        File propertysetsFile = new File(folder, "save_restore_cars_and_bikes.json");
        persister.persist(Files.newOutputStream(propertysetsFile.toPath()), context);
        var modelstore2 = new ModelstoreProvider();
        modelstore2.setValueCreator(valueCreator);
        modelstore2.activate();
        var nonMetadataRecordingContext2 = modelstore2.getDefaultContext();
        persister.restore(Files.newInputStream(propertysetsFile.toPath()), nonMetadataRecordingContext2);
        ModelContext context2 = new ModelContextRecordingMetadata(nonMetadataRecordingContext2, dateFactory, valueCreator);

        // Compare time stamps of restored context context3 with timstamps before storage (context2)
        Propertyset outback3 = context2.findPropertyset(outbackId);
        assertEquals(context.getLastmodifieddate(outback2), context2.getLastmodifieddate(outback3));
        Propertyset tacoma3 = context2.findPropertyset(tacomaId);
        assertEquals(context.getLastmodifieddate(tacoma2), context2.getLastmodifieddate(tacoma3));
        Propertyset volvo3 = context2.findPropertyset(volvoId);
        assertEquals(context.getLastmodifieddate(volvo2), context2.getLastmodifieddate(volvo3));
        Propertyset nakamura3 = context2.findPropertyset(nakamuraId);
        assertEquals(context.getLastmodifieddate(nakamura2), context2.getLastmodifieddate(nakamura3));
        Propertyset ferrari3 = context2.findPropertyset(ferrariId);
        assertEquals(context.getLastmodifieddate(ferrari2), context2.getLastmodifieddate(ferrari3));
    }

    /**
     * Unit test for {@link ModelContextRecordingMetadata#createList()}.
     */
    @Test
    void testCreateList() {
        ValueList list = context.createList();
        assertEquals(0, list.size());
        list.add(1);
        list.add(2);
        list.add(3.5);
        assertEquals(3, list.size());
    }

    /**
     * Unit test for {@link ModelContextRecordingMetadata#createPropertyset()}.
     */
    @Test
    void testCreatePropertyset() {
        Propertyset propertyset = context.createPropertyset();
        assertFalse(propertyset.hasId());
        assertEquals(0, propertyset.getPropertynames().size());
        propertyset.setBooleanProperty("a", true);
        propertyset.setLongProperty("b", 1);
        propertyset.setDoubleProperty("c", 1.1);
        assertEquals(3, propertyset.getPropertynames().size());
    }

    /**
     * Unit test for {@link ModelContextRecordingMetadata#listAllPropertysets()}.
     */
    @Test
    void testListAllPropertysets() {
        assertEquals(9, context.listAllPropertysets().size());
    }

    /**
     * Unit test for {@link ModelContextRecordingMetadata#listAllAspects()}.
     */
    @Test
    void testListAllAspects() {
        assertEquals(9, context.listAllAspects().size());
    }

    /**
     * Unit test for {@link ModelContextRecordingMetadata#findObjectsOfAspect(Propertyset)}.
     */
    @Test
    void testFindObjectsOfAspects() {
        assertEquals(5, context.findObjectsOfAspect(vehicleAspect).size());
        assertEquals(3, context.findObjectsOfAspect(valueCreator.unwrapPropertyset(carAspect)).size());
    }

    /**
     * Corner case unit test for {@link ModelContextRecordingMetadata#setLastmodifiedtimes(Propertyset)}.
     * Test unparsable {@link UUID} and {@link Date} values.
     */
    @Test
    void testSetLastmodfiedtimes() {
        var modelstore = new ModelstoreProvider();
        modelstore.setValueCreator(valueCreator);
        modelstore.activate();
        ModelContextRecordingMetadata context = (ModelContextRecordingMetadata) modelstore.createContext();
        Propertyset metadataWithErrors = createMetadataWithErrors(context);
        context.setLastmodifiedtimes(metadataWithErrors);

        assertEquals(2, modelstore.getErrors().size());
    }

    private void buildPropertysetB(ModelContext context, UUID bId) {
        Propertyset propertyset1 = context.findPropertyset(bId);
        propertyset1.setStringProperty("name", "b");
        propertyset1.setDoubleProperty("value", 1.2);
    }

    private Propertyset createMetadataWithErrors(ModelContext context) {
        Propertyset metadataWithErrors = context.createPropertyset();
        Propertyset lastmodifiedtimes = context.createPropertyset();
        lastmodifiedtimes.setStringProperty("not-a-uuid", "2015-06-16 22:37");
        lastmodifiedtimes.setStringProperty("be43c1da-229a-4b1e-9af6-8dbe98811586", "Not a parsable date");
        metadataWithErrors.setComplexProperty("lastmodifiedtimes", lastmodifiedtimes);
        return metadataWithErrors;
    }

    /**
     * Unit test for {@link ModelContextImpl#hashCode()}.
     */
    @Test
    void testHashCode() {
        var modelstore = mock(Modelstore.class);
        when(modelstore.getValueCreator()).thenReturn(valueCreator);
        ModelContext inner = new ModelContextImpl(modelstore);
        ModelContext context = new ModelContextRecordingMetadata(inner, dateFactory, valueCreator);
        assertEquals(-1866972311, context.hashCode());
        addPropertysetsToContext(inner);
        assertEquals(-267642137, context.hashCode());
    }

    /**
     * Unit test for {@link ModelContextImpl#equals()}.
     */
    @Test
    void testEquals() {
        var modelstore = mock(Modelstore.class);
        when(modelstore.getValueCreator()).thenReturn(valueCreator);
        ModelContext inner = new ModelContextImpl(modelstore);
        addPropertysetsToContext(inner);
        ModelContext context = new ModelContextRecordingMetadata(inner, dateFactory, valueCreator);
        assertEquals(context, context);
        assertNotNull(context);
        assertNotEquals(context, valueCreator.newPropertyset());
        assertNotEquals(context, new ModelContextRecordingMetadata(new ModelContextImpl(modelstore), dateFactory, valueCreator));
        ModelContext identicalInner = new ModelContextImpl(modelstore);
        addPropertysetsToContext(identicalInner);
        ModelContext identicalContext = new ModelContextRecordingMetadata(identicalInner, dateFactory, valueCreator);
        assertEquals(context, identicalContext);
    }

    /**
     * Unit test for {@link ModelContextImpl#toString()}.
     */
    @Test
    void testToString() {
        var modelstore = mock(Modelstore.class);
        when(modelstore.getValueCreator()).thenReturn(valueCreator);
        ModelContext inner = new ModelContextImpl(modelstore);
        addPropertysetsToContext(inner);
        ModelContext context = new ModelContextRecordingMetadata(inner, dateFactory, valueCreator);
        addPropertysetsToContext(context);
        assertThat(context.toString()).startsWith("ModelContextRecordingMetadata ");
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


}
