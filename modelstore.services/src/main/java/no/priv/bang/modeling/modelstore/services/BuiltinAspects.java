package no.priv.bang.modeling.modelstore.services;

import java.util.UUID;

/**
 * Contains the {@link UUID} ids of the builtin aspects that will be
 * present in all {@link ModelContext} objects.
 *
 */
public interface BuiltinAspects {
    /**
     * The "metadata" aspect defines a {@link Propertyset} that stores last modified times
     * and various other metadata on the propertysests in a {@link ModelContext}
     *
     * @return the {@link UUID} of the {@link Propertyset} defining the "metadata" aspect
     */
    UUID getMetadataAspectId();

    /**
     * The "general object" aspect defines a {@link Propertyset} with a name
     * and description.
     *
     * @return the {@link UUID} of the {@link Propertyset} defining the "general object" aspect
     */
    UUID getGeneralObjectAspectId();

    /**
     * The "relationship" aspect defines a {@link Propertyset} with reference
     * properties "origin" and "target" and can be used to represent a
     * relationship between two propertysets.
     *
     * @return the {@link UUID} of the {@link Propertyset} defining the "relationship" aspect
     */
    UUID getRelationshipAspectId();

    /**
     * The "general relationship" aspect inherits the "relationship" aspect and
     * adds a name and a description property.
     *
     * @return the {@link UUID} of the {@link Propertyset} defining the "general relationship" aspect
     */
    UUID getGeneralRelationshipAspectId();

    /**
     * The "model" aspect defines a model.  A model is a propertyset containing
     * other propertysets.
     *
     * @return the {@link UUID} of the {@link Propertyset} defining the "model" aspect
     */
    UUID getModelAspectId();

    /**
     * The "aspect container" aspect defines a propertyset that holds a list
     * of references to other aspect.  It also holds a reference to an
     * aspect definition.
     *
     * The aspect definition will be applied to all of the contained
     * propertysets.  One aspect can be contained by two separate aspect containers
     * and have different aspects in each container.
     *
     * @return the {@link UUID} of the {@link Propertyset} defining the "aspect container" aspect
     */
    UUID getAspectContainerAspectId();
}
