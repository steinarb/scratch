package no.priv.bang.modeling.modelstore.backend;

import static no.priv.bang.modeling.modelstore.backend.Values.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import no.priv.bang.modeling.modelstore.services.Value;
import no.priv.bang.modeling.modelstore.services.ValueList;

/**
 * Unit tests for {@link ListValue}.
 *
 * @author Steinar Bang
 *
 */
class ListValueTest {

    private ValueList valueList;
    private Value value;

    @BeforeEach
    void setUp() throws Exception {
        valueList = newList();
        valueList.add(toBooleanValue(Boolean.TRUE));
        valueList.add(toLongValue(42));
        valueList.add(toDoubleValue(2.78));
        valueList.add(toStringValue("foo bar"));
        value = toListValue(valueList, false);
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
        assertFalse(value.asBoolean());
    }

    @Test
    void testIsLong() {
        assertFalse(value.isLong());
    }

    @Test
    void testAsLong() {
        assertEquals(Long.valueOf(0), value.asLong());
    }

    @Test
    void testIsDouble() {
        assertFalse(value.isDouble());
    }

    @Test
    void testAsDouble() {
        assertEquals(Double.valueOf(0.0), value.asDouble());
    }

    @Test
    void testIsString() {
        assertFalse(value.isString());
    }

    @Test
    void testAsString() {
        assertEquals("", value.asString());
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
        assertTrue(value.isList());
    }

    @Test
    void testAsList() {
        ValueList list = value.asList();
        assertFalse(list.isEmpty());
        assertEquals(valueList.size(), list.size());
    }

    /**
     * Verify that a property value containing an empty list is
     * equal to the list extracted from a {@link NilValue}.
     */
    @Test
    void testEmptyListEqualsNilList() {
        Value emptylist = toListValue(newList(), false);
        Value nil = getNil();
        assertEquals(emptylist, nil);

        // TODO: should the equals be implemented in the nil object as well?
        assertNotEquals(nil, emptylist);
    }

    /**
     * Test av {@link ListValue#hashCode()}.
     */
    @Test
    void testHashCode() {
        Value nullListValue = toListValue(null);
        assertEquals(63, nullListValue.hashCode());
        Value foo = toListValue(newList(), false);
        assertEquals(63, foo.hashCode());
        assertEquals(-24528609, value.hashCode());
    }

    /**
     * Test av {@link ListValue#equals(Object)}.
     */
    @Test
    void testEquals() {
        Value nullListValue = toListValue(null);
        Value emptyvalue = toListValue(newList(), false);
        ValueList list = newList();
        list.add(toDoubleValue(3.14));
        Value otherValue = toListValue(list, false);
        assertNotEquals(nullListValue, null); // NOSONAR the point here is to test propertyset.equals, so no the arguments should not be swapped
        assertEquals(nullListValue, getNil());
        assertEquals(nullListValue, nullListValue);
        assertEquals(nullListValue, emptyvalue);
        assertNotEquals(emptyvalue, nullListValue);
        assertEquals(value, value);
        assertNotEquals(value, emptyvalue);
        assertNotEquals(value, nullListValue);
        assertNotEquals(emptyvalue, value);
        Value stringvalue = toStringValue("foobar");
        assertNotEquals(value, stringvalue);
        assertNotEquals(value, otherValue);
    }

    /**
     * Test av {@link ListValue#toString()}.
     */
    @Test
    void testToString() {
        Value nullListValue = toListValue(null);
        assertEquals("ListValue [value=[]]", nullListValue.toString());
        ValueList list = newList();
        list.add(toStringValue("foo"));
        list.add(toStringValue("bar"));
        list.add(toDoubleValue(2.78));
        Value otherValue = toListValue(list, false);
        assertEquals("ListValue [value=[StringValue [value=foo], StringValue [value=bar], DoubleValue [value=2.78]]]", otherValue.toString());
        assertEquals("ListValue [value=[BooleanValue [value=true], LongValue [value=42], DoubleValue [value=2.78], StringValue [value=foo bar]]]", value.toString());
    }

}
