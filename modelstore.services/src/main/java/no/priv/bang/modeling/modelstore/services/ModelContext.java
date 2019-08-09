package no.priv.bang.modeling.modelstore.services;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;

/**
 * A ModelContext is a reference to a set of interconnected
 * {@link Propertyset} instances forming a model or several models.
 *
 * A ModelContext can be persisted with
 * JsonPropertysetPersister#persist(java.io.File, ModelContext) and
 * restored with
 * JsonPropertysetPersister#restore(java.io.File, ModelContext).
 *
 * A {@link ModelContext} will keep track of the last modification
 * time of each {@link Propertyset} and when merging two ModelContexts
 * all {@link Propertyset}s will be merged, and when merging each
 * {@link Propertyset}, the {@link Propertyset} with the newest modification date
 * will win in case of conflict.
 *
 * @author Steinar Bang
 *
 */
public interface ModelContext {
    /**
     * Create a new empty {@link ValueList}.
     *
     * @return a {@link ValueList} instance
     */
    ValueList createList();

    /**
     * Create a new {@link Propertyset} instance that has no id
     * and can be used as a complex property value of another
     * {@link Propertyset}.
     *
     * @return a {@link Propertyset} instance
     */
    Propertyset createPropertyset();

    /**
     * Return a {@link Propertyset} with a {@link Propertyset#getId()}
     * matching the argument. If no matching {@link Propertyset} can
     * be found a new one will be created, empty except for the id.
     *
     * @param id a {@link UUID} identifying a {@link Propertyset}
     * @return a {@link Propertyset} matching the id argument
     */
    Propertyset findPropertyset(UUID id);

    /**
     * List all of the {@link Propertyset} instances with ids,
     * except for the aspects that is built into every
     * {@link Propertyset}.
     *
     * @return a collection of {@link Propertyset} instances
     */
    Collection<Propertyset> listAllPropertysets();

    /**
     * List all of the {@link Propertyset} instances with ids
     * that contain aspect descriptions in this context.
     *
     * Aspect definitons follow a structure similar to
     * JSON schemas.
     *
     * @return a collection of {@link Propertyset} instances containing aspect definitions.
     */
    Collection<Propertyset> listAllAspects();

    /**
     * Find all of the {@link Propertyset} instances that have had
     * a particular aspect applied to it.  If the aspect is a base
     * object for other aspects, propertysets matching the child
     * aspects will also be returned.
     *
     * @param aspect the aspect to find propertysets matching
     * @return a collection of {@link Propertyset} instances
     */
    Collection<Propertyset> findObjectsOfAspect(Propertyset aspect);

    /**
     * Get the last modified date and time of a propertyset.
     *
     * @param propertyset the {@link Propertyset} to find the last modified time for
     * @return date and time of the last modification for the propertyset, or null if no modification date could be found
     */
    Date getLastmodifieddate(Propertyset propertyset);

    void merge(ModelContext otherContext);

    /**
     * Log an error situation that resulted in a caught exception.
     *
     * @param message a human readable message explaining where the error occurred
     * @param fileOrStream a File or stream involved in the error, null if not relevant
     * @param e the exception caught by the code logging the error
     */
    void logError(String message, Object fileOrStream, Exception e);
}
