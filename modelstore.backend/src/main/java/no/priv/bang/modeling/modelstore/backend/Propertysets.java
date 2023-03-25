package no.priv.bang.modeling.modelstore.backend;

import no.priv.bang.modeling.modelstore.services.Propertyset;

/**
 * Static methods for manipulating {@link Propertyset} instances.
 *
 * @author Steinar Bang
 *
 */
public class Propertysets {

    // Shared constants for Propertyset implementations
    public static final String idKey = "id";
    public static final String aspectsKey = "aspects";

    /**
     * If the {@link Propertyset} argument's implementation is wrapping a
     * different {@link Propertyset} (e.g. {@link PropertysetRecordingSaveTime}
     * wrapping a {@link PropertysetImpl}), then the wrapped {@link Propertyset}
     * is returned.  If the {@link Propertyset} isn't wrapping a different
     * {@link Propertyset}, the argument itself is returned.
     *
     * @param propertyset is a {@link Propertyset} that may or may not wrap a different {@link Propertyset}
     * @return the wrapped {@link Propertyset} if the argument wraps a {@link Propertyset} or the argument itself it it doesn't wrap another {@link Propertyset}
     */
    public static Propertyset findWrappedPropertyset(Propertyset propertyset) {
        if (propertyset instanceof PropertysetRecordingSaveTime) {
            PropertysetRecordingSaveTime wrapper = (PropertysetRecordingSaveTime) propertyset;
            return wrapper.getPropertyset();
        }

        return propertyset;
    }

    private Propertysets() { /* static class */ }

}
