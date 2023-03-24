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
public class ListValueTest {

    private ValueList valueList;
    private Value value;

    @BeforeEach
    public void setUp() throws Exception {
        valueList = newList();
        valueList.add(toBooleanValue(Boolean.TRUE));
        valueList.add(toLongValue(42));
        valueList.add(toDoubleValue(2.78));
        valueList.add(toStringValue("foo bar"));
        value = toListValue(valueList, false);
    }

    @Test
    public void testIsId() {
        assertFalse(value.isId());
    }

    @Test
    public void testAsId() {
        assertEquals(getNil().asId(), value.asId());
    }

    @Test
    public void testIsBoolean() {
        assertFalse(value.isBoolean());
    }

    @Test
    public void testAsBoolean() {
        assertFalse(value.asBoolean());
    }

    @Test
    public void testIsLong() {
        assertFalse(value.isLong());
    }

    @Test
    public void testAsLong() {
        assertEquals(Long.valueOf(0), value.asLong());
    }

    @Test
    public void testIsDouble() {
        assertFalse(value.isDouble());
    }

    @Test
    public void testAsDouble() {
        assertEquals(Double.valueOf(0.0), value.asDouble());
    }

    @Test
    public void testIsString() {
        assertFalse(value.isString());
    }

    @Test
    public void testAsString() {
        assertEquals("", value.asString());
    }

    @Test
    public void testIsComplexProperty() {
        assertFalse(value.isComplexProperty());
    }

    @Test
    public void testAsComplexProperty() {
        assertEquals(getNilPropertyset(), value.asComplexProperty());
    }

    @Test
    public void testIsReference() {
        assertFalse(value.isReference());
    }

    @Test
    public void testAsReference() {
        assertEquals(getNilPropertyset(), value.asReference());
    }

    @Test
    public void testIsList() {
        assertTrue(value.isList());
    }

    @Test
    public void testAsList() {
        ValueList list = value.asList();
        assertFalse(list.isEmpty());
        assertEquals(valueList.size(), list.size());
    }

    /**
     * Verify that a property value containing an empty list is
     * equal to the list extracted from a {@link NilValue}.
     */
    @Test
    public void testEmptyListEqualsNilList() {
        Value emptylist = toListValue(newList(), false);
        Value nil = getNil();
        assertTrue(emptylist.equals(nil));

        // TODO: should the equals be implemented in the nil object as well?
        assertFalse(nil.equals(emptylist));
    }

    /**
     * Test av {@link ListValue#hashCode()}.
     */
    @Test
    public void testHashCode() {
        Value nullListValue = toListValue(null);
        assertEquals(32, nullListValue.hashCode());
        Value foo = toListValue(newList(), false);
        assertEquals(63, foo.hashCode());
        assertEquals(-24528609, value.hashCode());
    }

    /**
     * Test av {@link ListValue#equals(Object)}.
     */
    @Test
    public void testEquals() {
        Value nullListValue = toListValue(null);
        Value emptyvalue = toListValue(newList(), false);
        ValueList list = newList();
        list.add(toDoubleValue(3.14));
        Value otherValue = toListValue(list, false);
        assertFalse(nullListValue.equals(null));
        assertTrue(nullListValue.equals(getNil()));
        assertTrue(nullListValue.equals(nullListValue));
        assertTrue(nullListValue.equals(emptyvalue));
        assertFalse(emptyvalue.equals(nullListValue));
        assertTrue(value.equals(value));
        assertFalse(value.equals(emptyvalue));
        assertFalse(value.equals(nullListValue));
        assertFalse(emptyvalue.equals(value));
        Value stringvalue = toStringValue("foobar");
        assertFalse(value.equals(stringvalue));
        assertFalse(value.equals(otherValue));
    }

    /**
     * Test av {@link ListValue#toString()}.
     */
    @Test
    public void testToString() {
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
