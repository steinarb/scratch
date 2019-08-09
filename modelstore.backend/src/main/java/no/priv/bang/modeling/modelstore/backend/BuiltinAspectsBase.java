package no.priv.bang.modeling.modelstore.backend;

import java.util.UUID;

import no.priv.bang.modeling.modelstore.services.BuiltinAspects;

/**
 * Return the IDs of the aspects that are always present in the
 * ModelContext objects.
 *
 * This class implements {@link BuiltinAspects} and is intended to
 * be used either as an OSGi service alone or as a base class
 * for the Modelstore implementation.
 *
 * @author Steinar Bang
 *
 */
public class BuiltinAspectsBase implements BuiltinAspects {

    /* (non-Javadoc)
     * @see no.priv.bang.modeling.modelstore.BuiltinAspects#getMetadataAspectId()
     */
    public UUID getMetadataAspectId() {
        return Aspects.metadataAspectId;
    }

    /* (non-Javadoc)
     * @see no.priv.bang.modeling.modelstore.BuiltinAspects#getGeneralObjectAspectId()
     */
    public UUID getGeneralObjectAspectId() {
        return Aspects.generalObjectAspectId;
    }

    /* (non-Javadoc)
     * @see no.priv.bang.modeling.modelstore.BuiltinAspects#getRelationshipAspectId()
     */
    public UUID getRelationshipAspectId() {
        return Aspects.relationshipAspectId;
    }

    /* (non-Javadoc)
     * @see no.priv.bang.modeling.modelstore.BuiltinAspects#getGeneralRelationshipAspectId()
     */
    public UUID getGeneralRelationshipAspectId() {
        return Aspects.generalRelationshipAspectId;
    }

    /* (non-Javadoc)
     * @see no.priv.bang.modeling.modelstore.BuiltinAspects#getModelAspectId()
     */
    public UUID getModelAspectId() {
        return Aspects.modelAspectId;
    }

    /* (non-Javadoc)
     * @see no.priv.bang.modeling.modelstore.BuiltinAspects#getAspectContainerAspectId()
     */
    public UUID getAspectContainerAspectId() {
        return Aspects.aspectContainerAspectId;
    }

}
