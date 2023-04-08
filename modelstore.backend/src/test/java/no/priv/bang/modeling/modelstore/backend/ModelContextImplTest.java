package no.priv.bang.modeling.modelstore.backend;

import static no.priv.bang.modeling.modelstore.backend.Aspects.*;
import static no.priv.bang.modeling.modelstore.testutils.TestUtils.compareAllPropertysets;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.fasterxml.jackson.core.JsonFactory;

import no.priv.bang.modeling.modelstore.services.ModelContext;
import no.priv.bang.modeling.modelstore.services.Propertyset;
import no.priv.bang.modeling.modelstore.value.ValueCreatorProvider;

/**
 * Unit tests for class {@link ModelContextImpl}.
 *
 */
class ModelContextImplTest {
    private ValueCreatorProvider valueCreator;
    private ModelstoreProvider modelstore;

    @TempDir
    File folder;

    @BeforeEach
    void setup() {
        valueCreator = new ValueCreatorProvider();
        modelstore = new ModelstoreProvider();
        modelstore.setValueCreator(valueCreator);
        modelstore.activate();
    }

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
    void testMergeNoOverlapBetweenContexts() throws IOException {
        ModelContext context = new ModelContextImpl(modelstore);
        buildPropertysetA(context, UUID.randomUUID());
        assertEquals(1, context.listAllPropertysets().size(), "Expected context to contain 1 propertyset");

        ModelContext otherContext = new ModelContextImpl(modelstore);
        UUID bId = UUID.randomUUID();
        buildPropertysetB(otherContext, bId);
        assertEquals(1, otherContext.listAllPropertysets().size(), "Expected otherContext to contain 1 propertyset");

        context.merge(otherContext);
        assertEquals(2, context.listAllPropertysets().size(), "Expected context to contain 2 propertysets");
        // Verify that the copied "B" is the same as the original B
        // TODO decide if PropertysetRecordingSaveTime.equals() should include the context in comparison, for now: get the inner PropertysetImpl instances and compare them instead
        Propertyset originalB = valueCreator.unwrapPropertyset(otherContext.findPropertyset(bId));
        Propertyset mergedB = valueCreator.unwrapPropertyset(context.findPropertyset(bId));
        assertEquals(originalB, mergedB);

        // Save and restore the merged context and verify that the restored context is the same as the merged context
        File propertysetsFile = new File(folder, "mergedcontext.json");
        OutputStream saveStream = Files.newOutputStream(propertysetsFile.toPath());
        JsonFactory factory = new JsonFactory();
        JsonPropertysetPersister persister = new JsonPropertysetPersister(factory, null);
        persister.persist(saveStream, context);
        InputStream loadStream = Files.newInputStream(propertysetsFile.toPath());
        ModelContext restoredContext = new ModelContextImpl(modelstore);
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
    void testMergeWithOverlapBetweenContexts() throws IOException, InterruptedException {
        ModelContext context = new ModelContextImpl(modelstore);
        UUID aId = UUID.randomUUID();
        buildPropertysetA(context, aId);
        assertEquals(1, context.listAllPropertysets().size(), "Expected context to contain 1 propertyset");

        ModelContext otherContext = new ModelContextImpl(modelstore);
        UUID bId = UUID.randomUUID();
        buildPropertysetA(otherContext, aId);
        otherContext.findPropertyset(aId).setLongProperty("value", 42);
        Propertyset generalObjectAspect = otherContext.findPropertyset(generalObjectAspectId);
        otherContext.findPropertyset(aId).addAspect(generalObjectAspect);
        buildPropertysetB(otherContext, bId);
        otherContext.findPropertyset(bId).addAspect(generalObjectAspect);
        assertEquals(2, otherContext.listAllPropertysets().size(), "Expected otherContext to contain 2 propertysets");

        buildPropertysetB(context, bId);
        context.findPropertyset(bId).setLongProperty("value", 4); // Change the value, should be kept after merge
        Propertyset modelAspect = context.findPropertyset(modelAspectId);
        context.findPropertyset(bId).addAspect(modelAspect);
        assertEquals(2, context.listAllPropertysets().size(), "Expected context to contain 2 propertysets");

        context.merge(otherContext);

        // Verify the merge results
        assertEquals(2, context.listAllPropertysets().size(), "Expected context to contain 2 propertysets");
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
        File propertysetsFile = new File(folder, "mergedcontext.json");
        OutputStream saveStream = Files.newOutputStream(propertysetsFile.toPath());
        JsonFactory factory = new JsonFactory();
        JsonPropertysetPersister persister = new JsonPropertysetPersister(factory, null);
        persister.persist(saveStream, context);
        InputStream loadStream = Files.newInputStream(propertysetsFile.toPath());
        ModelContext restoredContext = new ModelContextImpl(modelstore);
        persister.restore(loadStream, restoredContext);
        compareAllPropertysets(context, restoredContext);
    }

    /**
     * Unit test for {@link ModelContextImpl#hashCode()}.
     */
    @Test
    void testHashCode() {
        ModelContext context = new ModelContextImpl();
        assertEquals(216866173, context.hashCode());
        addPropertysetsToContext(context);
        assertEquals(-1809752513, context.hashCode());
    }

    /**
     * Unit test for {@link ModelContextImpl#equals()}.
     */
    @Test
    void testEquals() {
        ModelContext context = new ModelContextImpl(modelstore);
        addPropertysetsToContext(context);
        assertEquals(context, context);
        assertNotEquals(context, null); // NOSONAR the point here is to test propertyset.equals, so no the arguments should not be swapped
        assertNotEquals(context, valueCreator.newPropertyset());
        ModelContext identicalContext = new ModelContextImpl(modelstore);
        addPropertysetsToContext(identicalContext);
        assertEquals(context, identicalContext);
    }

    /**
     * Unit test for {@link ModelContextImpl#toString()}.
     */
    @Test
    void testToString() {
        ModelContext context = new ModelContextImpl(modelstore);
        addPropertysetsToContext(context);
        assertThat(context.toString()).startsWith("ModelContextImpl ");
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
