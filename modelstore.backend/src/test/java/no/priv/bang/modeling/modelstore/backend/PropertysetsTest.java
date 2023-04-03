package no.priv.bang.modeling.modelstore.backend;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import static no.priv.bang.modeling.modelstore.backend.Propertysets.*;
import static org.junit.jupiter.api.Assertions.*;

import no.priv.bang.modeling.modelstore.services.ModelContext;
import no.priv.bang.modeling.modelstore.services.Modelstore;
import no.priv.bang.modeling.modelstore.services.Propertyset;

/**
 * Unit tests for class {@link Propertysets}.
 *
 */
class PropertysetsTest {
    /**
     * Unit tests for {@link Propertysets#findWrappedPropertyset(no.priv.bang.modeling.modelstore.Propertyset)}.
     */
    @Test
    void testFindWrappedPropertyset() {
        Modelstore modelstore = new ModelstoreProvider();
        ModelContext context = modelstore.createContext();

        // Test unwrapping of a wrapped propertyset
        Propertyset wrappedPropertyset = context.findPropertyset(UUID.randomUUID());
        Propertyset unwrappedPropertyset = findWrappedPropertyset(wrappedPropertyset);
        assertNotSame(wrappedPropertyset, unwrappedPropertyset); // Not the same object
        assertEquals(wrappedPropertyset, unwrappedPropertyset); // A wrapper tests equal to the propertyset it wraps

        // Test that an unwrapped propertyset comes through as itself
        Propertyset unwrappedPropertyset2 = findWrappedPropertyset(unwrappedPropertyset);
        assertSame(unwrappedPropertyset, unwrappedPropertyset2);
    }
}
