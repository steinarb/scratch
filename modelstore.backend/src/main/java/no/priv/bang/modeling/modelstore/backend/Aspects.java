package no.priv.bang.modeling.modelstore.backend;

import java.util.UUID;

/**
 * Constants and static functions related to aspects, which are
 * Propertyset instances defining Propertyset schemas.
 *
 * @author Steinar Bang
 *
 */
public class Aspects {
    // IDs of built-in aspects
    public static final UUID metadataAspectId = UUID.fromString("ad7ac2e9-70ee-4b1d-8529-d3ed78806714");
    public static final UUID generalObjectAspectId = UUID.fromString("06cee83c-2ca8-44b8-8035-c79586665532");
    public static final UUID relationshipAspectId = UUID.fromString("8ecdaddb-7452-4eb1-82e9-451d2b6b9b5c");
    public static final UUID generalRelationshipAspectId = UUID.fromString("d01a2f8a-6a09-4d5d-bf5d-c29df62761ca");
    public static final UUID modelAspectId = UUID.fromString("dae363e5-4757-4be7-b5d6-c3e7409c472c");
    public static final UUID aspectContainerAspectId = UUID.fromString("48168b6e-808b-4fe7-9997-c4eadeda2c6d");

    private Aspects() { /* static class */ }
}
