package no.priv.bang.modeling.modelstore.value;

import static no.priv.bang.modeling.modelstore.value.Values.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import no.priv.bang.modeling.modelstore.services.Value;
import no.priv.bang.modeling.modelstore.services.ValueList;

/**
 * Unit tests for {@link DoubleValue}.
 *
 */
class DoubleValueTest {

    private Value value;

    @BeforeEach
    void setUp() throws Exception {
        value = toDoubleValue(3.14);
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
        assertFalse(value.isLong());
    }

    @Test
    void testAsLong() {
        assertEquals(Long.valueOf(3), value.asLong());
    }

    @Test
    void testIsDouble() {
        assertTrue(value.isDouble());
    }

    @Test
    void testAsDouble() {
        assertEquals(Double.valueOf(3.14), value.asDouble());
    }

    @Test
    void testIsString() {
        assertFalse(value.isString());
    }

    @Test
    void testAsString() {
        assertEquals("3.14", value.asString());
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
     * Test av {@link DoubleValue#hashCode()}.
     */
    @Test
    void testHashCode() {
        Value nullDoubleValue = toDoubleValue(null);
        assertEquals(31, nullDoubleValue.hashCode());
        Value pi = toDoubleValue(3.14);
        assertEquals(300063686, pi.hashCode());
    }

    /**
     * Test av {@link DoubleValue#equals(Object)}.
     */
    @Test
    void testEquals() {
        Value nullDoubleValue = toDoubleValue(null);
        assertNotEquals(nullDoubleValue, null); // NOSONAR the point here is to test propertyset.equals, so no the arguments should not be swapped
        assertNotEquals(nullDoubleValue, getNil());
        assertEquals(nullDoubleValue, nullDoubleValue);
        assertNotEquals(nullDoubleValue, value);
        assertNotEquals(value, nullDoubleValue);
    }

    /**
     * Test av {@link DoubleValue#toString()}.
     */
    @Test
    void testToString() {
        Value nullDoubleValue = toDoubleValue(null);
        assertEquals("DoubleValue [value=0.0]", nullDoubleValue.toString());
        Value e = toDoubleValue(2.78);
        assertEquals("DoubleValue [value=2.78]", e.toString());
    }

}
