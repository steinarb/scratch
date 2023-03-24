/**
 *
 */
package no.priv.bang.modeling.modelstore.backend;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import no.priv.bang.modeling.modelstore.services.ModelContext;
import no.priv.bang.modeling.modelstore.services.Modelstore;
import no.priv.bang.modeling.modelstore.services.Propertyset;

/**
 * Unit tests for {@link BuiltinAspectsBase}
 *
 * @author Steinar Bang
 *
 */
public class BuiltinAspectsBaseTest {

    private Modelstore modelstore;
    private ModelContext context;

    /**
     * @throws java.lang.Exception
     */
    @BeforeEach
    public void setUp() {
        modelstore = new ModelstoreProvider();
        context = modelstore.createContext();
    }

    /**
     * Unit test for {@link BuiltinAspectsBase#getMetadataAspectId()}
     */
    @Test
    public void testGetMetadataAspectId() {
        Propertyset metadataAspect = context.findPropertyset(modelstore.getMetadataAspectId());
        assertEquals("metadata", metadataAspect.getStringProperty("title"));
    }

    /**
     * Unit test for {@link BuiltinAspectsBase#getGeneralObjectAspectId()}
     */
    @Test
    public void testGetGeneralObjectAspectId() {
        Propertyset generalObjectAspect = context.findPropertyset(modelstore.getGeneralObjectAspectId());
        assertEquals("general object", generalObjectAspect.getStringProperty("title"));
    }

    /**
     * Unit test for {@link BuiltinAspectsBase#getRelationshipAspectId()}
     */
    @Test
    public void testGetRelationshipAspectId() {
        Propertyset relationshipAspect = context.findPropertyset(modelstore.getRelationshipAspectId());
        assertEquals("relationship", relationshipAspect.getStringProperty("title"));
    }

    /**
     * Unit test for {@link BuiltinAspectsBase#getGeneralRelationshipAspectId()}
     */
    @Test
    public void testGetGeneralRelationshipAspectId() {
        Propertyset generalRelationshipAspect = context.findPropertyset(modelstore.getGeneralRelationshipAspectId());
        assertEquals("general relationship", generalRelationshipAspect.getStringProperty("title"));
    }

    /**
     * Unit test for {@link BuiltinAspectsBase#getModelAspectId()}
     */
    @Test
    public void testGetModelAspectId() {
        Propertyset modelAspect = context.findPropertyset(modelstore.getModelAspectId());
        assertEquals("model", modelAspect.getStringProperty("title"));
    }

    /**
     * Unit test for {@link BuiltinAspectsBase#getAspectContainerAspectId()}
     */
    @Test
    public void testGetAspectContainerAspectId() {
        Propertyset aspectContainerAspect = context.findPropertyset(modelstore.getAspectContainerAspectId());
        assertEquals("aspect container", aspectContainerAspect.getStringProperty("title"));
    }

}
