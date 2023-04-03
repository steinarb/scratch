package no.priv.bang.modeling.modelstore.services;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * An interface that defines the access to all {@link Propertyset} instances
 * in memory.
 *
 * This interface can be used to create new propertysets, and it can be used
 * to retrieve existing propertysets.
 *
 */
public interface Modelstore extends BuiltinAspects {

    ModelContext getDefaultContext();
    ModelContext createContext();
    ModelContext restoreContext(InputStream jsonfilestream);
    void persistContext(OutputStream jsonfilestream, ModelContext context);

    /**
     * Log an error situation that resulted in a caught exception.
     *
     * @param message a human readable message explaining where the error occurred
     * @param fileOrStream a File or stream involved in the error, null if not relevant
     * @param e the exception caught by the code logging the error
     */
    void logError(String message, Object fileOrStream, Exception e);

    /**
     * Get the list of logged errors
     *
     * @return a list of {@link ErrorBean}, return the empty list if no errors have been reported
     */
    List<ErrorBean> getErrors();

}
