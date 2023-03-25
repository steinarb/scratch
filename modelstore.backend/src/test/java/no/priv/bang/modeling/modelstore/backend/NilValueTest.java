package no.priv.bang.modeling.modelstore.backend;

import no.priv.bang.modeling.modelstore.services.ValueList;

import static no.priv.bang.modeling.modelstore.backend.Values.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link NilValue}.
 *
 * @author Steinar Bang
 *
 */
class NilValueTest {

    @Test
    void testIsId() {
        assertFalse(getNil().isId());
    }

    @Test
    void testIsBoolean() {
        assertFalse(getNil().isBoolean());
    }

    @Test
    void testAsBoolean() {
        assertFalse(getNil().asBoolean());
        assertEquals(Boolean.valueOf(false), getNil().asBoolean());
    }

    @Test
    void testIsLong() {
        assertFalse(getNil().isLong());
    }

    @Test
    void testAsLong() {
        assertEquals(Long.valueOf(0), getNil().asLong());
    }

    @Test
    void testIsDouble() {
        assertFalse(getNil().isDouble());
    }

    @Test
    void testAsDouble() {
        assertEquals(Double.valueOf(0.0), getNil().asDouble());
    }

    @Test
    void testIsString() {
        assertFalse(getNil().isString());
    }

    @Test
    void testAsString() {
        assertEquals("", getNil().asString());
    }

    @Test
    void testIsComplexProperty() {
        assertFalse(getNil().isComplexProperty());
    }

    @Test
    void testAsComplexProperty() {
        assertEquals(getNilPropertyset(), getNil().asComplexProperty());
    }

    @Test
    void testIsReference() {
        assertFalse(getNil().isReference());
    }

    @Test
    void testAsReference() {
        assertEquals(getNilPropertyset(), getNil().asReference());
    }

    @Test
    void testIsList() {
        assertFalse(getNil().isList());
    }

    @Test
    void testAsList() {
        ValueList emptyList = getNil().asList();
        assertTrue(emptyList.isEmpty());
        assertEquals(0, emptyList.size());

        // Verify that the list can't be modified.
        emptyList.add(toBooleanValue(true));
        emptyList.add(toLongValue(13));
        emptyList.add(toDoubleValue(2.78));
        emptyList.add(toStringValue("foo bar!"));
        assertTrue(emptyList.isEmpty());
        assertEquals(0, emptyList.size());
    }

    @Test
    void testHashCode() {
        assertEquals(0, getNil().hashCode());
    }

    @Test
    void testToString() {
        assertEquals("NilValue []", getNil().toString());
    }

}
