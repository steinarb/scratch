package no.priv.bang.modeling.modelstore.value;

import static no.priv.bang.modeling.modelstore.value.Values.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import no.priv.bang.modeling.modelstore.services.Value;
import no.priv.bang.modeling.modelstore.services.ValueList;

/**
 * Unit tests for {@link LongValue}.
 *
 */
class LongValueTest {

    private Value value;

    @BeforeEach
    void setUp() throws Exception {
        value = toLongValue(42);
    }

    @Test
    void testIsId() {
        assertFalse(value.isId());
    }

    @Test
    void testAsId() {
        assertEquals(getNil().asId(), value.asId());
    }

    @Test
    void testIsBoolean() {
        assertFalse(value.isBoolean());
    }

    @Test
    void testAsBoolean() {
        assertTrue(value.asBoolean());
    }

    @Test
    void testIsLong() {
        assertTrue(value.isLong());
    }

    @Test
    void testAsLong() {
        assertEquals(Long.valueOf(42), value.asLong());
    }

    @Test
    void testIsDouble() {
        assertFalse(value.isDouble());
    }

    @Test
    void testAsDouble() {
        assertEquals(Double.valueOf(42.0), value.asDouble());
    }

    @Test
    void testIsString() {
        assertFalse(value.isString());
    }

    @Test
    void testAsString() {
        assertEquals("42", value.asString());
    }

    @Test
    void testIsComplexProperty() {
        assertFalse(value.isComplexProperty());
    }

    @Test
    void testAsComplexProperty() {
        assertEquals(getNilPropertyset(), value.asComplexProperty());
    }

    @Test
    void testIsReference() {
        assertFalse(value.isReference());
    }

    @Test
    void testAsReference() {
        assertEquals(getNilPropertyset(), value.asReference());
    }

    @Test
    void testIsList() {
        assertFalse(value.isList());
    }

    @Test
    void testAsList() {
        ValueList emptyList = value.asList();
        assertTrue(emptyList.isEmpty());
    }

    /**
     * Test av {@link LongValue#hashCode()}.
     */
    @Test
    void testHashCode() {
        Value nullLongValue = toLongValue(null);
        assertEquals(31, nullLongValue.hashCode());
        assertEquals(73, value.hashCode());
    }

    /**
     * Test av {@link LongValue#equals(Object)}.
     */
    @Test
    void testEquals() {
        Value nullLongValue = toLongValue(null);
        assertNotEquals(nullLongValue, null); // NOSONAR the point here is to test propertyset.equals, so no the arguments should not be swapped
        assertNotEquals(nullLongValue, getNil());
        assertEquals(nullLongValue, nullLongValue);
        assertNotEquals(nullLongValue, value);
        assertNotEquals(value, nullLongValue);
    }

    /**
     * Test av {@link LongValue#toString()}.
     */
    @Test
    void testToString() {
        Value nullLongValue = toLongValue(null);
        assertEquals("LongValue [value=0]", nullLongValue.toString());
        assertEquals("LongValue [value=42]", value.toString());
    }

}
