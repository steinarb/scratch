package no.priv.bang.modeling.modelstore.backend;

import static no.priv.bang.modeling.modelstore.backend.Values.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import no.priv.bang.modeling.modelstore.services.Value;
import no.priv.bang.modeling.modelstore.services.ValueList;

/**
 * Unit tests for {@link StringValue}.
 *
 * @author Steinar Bang
 *
 */
class StringValueTest {

    private Value valueWithNumber;
    private Value valueNotANumber;

    @BeforeEach
    void setUp() throws Exception {
        valueWithNumber = toStringValue("13");
        valueNotANumber = toStringValue("Not a number");
    }

    @Test
    void testIsId() {
        assertFalse(valueWithNumber.isId());
        assertFalse(valueNotANumber.isId());
    }

    @Test
    void testAsId() {
        assertEquals(getNil().asId(), valueWithNumber.asId());
        assertEquals(getNil().asId(), valueNotANumber.asId());
    }

    @Test
    void testIsBoolean() {
        assertFalse(valueWithNumber.isBoolean());
        assertFalse(valueNotANumber.isBoolean());
    }

    @Test
    void testAsBoolean() {
        assertFalse(valueWithNumber.asBoolean());
        assertFalse(valueNotANumber.asBoolean());
    }

    @Test
    void testIsLong() {
        assertFalse(valueWithNumber.isLong());
        assertFalse(valueNotANumber.isLong());
    }

    @Test
    void testAsLong() {
        assertEquals(Long.valueOf(13), valueWithNumber.asLong());
        assertEquals(Long.valueOf(0), valueNotANumber.asLong());
    }

    @Test
    void testIsDouble() {
        assertFalse(valueWithNumber.isDouble());
        assertFalse(valueNotANumber.isDouble());
    }

    @Test
    void testAsDouble() {
        assertEquals(Double.valueOf(13.0), valueWithNumber.asDouble());
        assertEquals(Double.valueOf(0.0), valueNotANumber.asDouble());
    }

    @Test
    void testIsString() {
        assertTrue(valueWithNumber.isString());
        assertTrue(valueNotANumber.isString());
    }

    @Test
    void testAsString() {
        assertEquals("13", valueWithNumber.asString());
        assertEquals("Not a number", valueNotANumber.asString());
    }

    @Test
    void testIsComplexProperty() {
        assertFalse(valueWithNumber.isComplexProperty());
        assertFalse(valueNotANumber.isComplexProperty());
    }

    @Test
    void testAsComplexProperty() {
        assertEquals(getNilPropertyset(), valueWithNumber.asComplexProperty());
        assertEquals(getNilPropertyset(), valueNotANumber.asComplexProperty());
    }

    @Test
    void testIsReference() {
        assertFalse(valueWithNumber.isReference());
        assertFalse(valueNotANumber.isReference());
    }

    @Test
    void testAsReference() {
        assertEquals(getNilPropertyset(), valueWithNumber.asReference());
        assertEquals(getNilPropertyset(), valueNotANumber.asReference());
    }

    @Test
    void testIsList() {
        assertFalse(valueWithNumber.isList());
        assertFalse(valueNotANumber.isList());
    }

    @Test
    void testAsList() {
        ValueList emptyList1 = valueWithNumber.asList();
        assertTrue(emptyList1.isEmpty());
        ValueList emptyList2 = valueNotANumber.asList();
        assertTrue(emptyList2.isEmpty());
    }

    /**
     * Test av {@link StringValue#hashCode()}.
     */
    @Test
    void testHashCode() {
        Value nullStringValue = toStringValue(null);
        assertEquals(31, nullStringValue.hashCode());
        Value foo = toStringValue("foo");
        assertEquals(101605, foo.hashCode());
    }

    /**
     * Test av {@link StringValue#equals(Object)}.
     */
    @Test
    void testEquals() {
        Value nullStringValue = toStringValue(null);
        Value value = toStringValue("foobar");
        assertFalse(nullStringValue.equals(null));
        assertFalse(nullStringValue.equals(getNil()));
        assertTrue(nullStringValue.equals(nullStringValue));
        assertFalse(nullStringValue.equals(value));
        assertFalse(value.equals(nullStringValue));
    }

    /**
     * Test av {@link StringValue#toString()}.
     */
    @Test
    void testToString() {
        Value nullLongValue = toStringValue(null);
        assertEquals("StringValue [value=]", nullLongValue.toString());
        Value value = toStringValue("bar");
        assertEquals("StringValue [value=bar]", value.toString());
    }

}
