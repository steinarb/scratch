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
public class NilValueTest {

    @Test
    public void testIsId() {
        assertFalse(getNil().isId());
    }

    @Test
    public void testIsBoolean() {
        assertFalse(getNil().isBoolean());
    }

    @Test
    public void testAsBoolean() {
        assertFalse(getNil().asBoolean());
        assertEquals(Boolean.valueOf(false), getNil().asBoolean());
    }

    @Test
    public void testIsLong() {
        assertFalse(getNil().isLong());
    }

    @Test
    public void testAsLong() {
        assertEquals(Long.valueOf(0), getNil().asLong());
    }

    @Test
    public void testIsDouble() {
        assertFalse(getNil().isDouble());
    }

    @Test
    public void testAsDouble() {
        assertEquals(Double.valueOf(0.0), getNil().asDouble());
    }

    @Test
    public void testIsString() {
        assertFalse(getNil().isString());
    }

    @Test
    public void testAsString() {
        assertEquals("", getNil().asString());
    }

    @Test
    public void testIsComplexProperty() {
        assertFalse(getNil().isComplexProperty());
    }

    @Test
    public void testAsComplexProperty() {
        assertEquals(getNilPropertyset(), getNil().asComplexProperty());
    }

    @Test
    public void testIsReference() {
        assertFalse(getNil().isReference());
    }

    @Test
    public void testAsReference() {
        assertEquals(getNilPropertyset(), getNil().asReference());
    }

    @Test
    public void testIsList() {
        assertFalse(getNil().isList());
    }

    @Test
    public void testAsList() {
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
    public void testHashCode() {
        assertEquals(0, getNil().hashCode());
    }

    @Test
    public void testToString() {
        assertEquals("NilValue []", getNil().toString());
    }

}
