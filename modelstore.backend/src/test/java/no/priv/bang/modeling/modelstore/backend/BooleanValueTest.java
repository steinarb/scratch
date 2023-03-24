package no.priv.bang.modeling.modelstore.backend;

import static no.priv.bang.modeling.modelstore.backend.Values.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import no.priv.bang.modeling.modelstore.services.Value;
import no.priv.bang.modeling.modelstore.services.ValueList;

/**
 * Unit tests for {@link BooleanValue}.
 *
 * @author Steinar Bang
 *
 */
public class BooleanValueTest {

    private Value value;

    @BeforeEach
    public void setUp() throws Exception {
        value = toBooleanValue(Boolean.TRUE);
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
        assertTrue(value.isBoolean());
    }

    @Test
    public void testAsBoolean() {
        assertTrue(value.asBoolean());
    }

    @Test
    public void testIsLong() {
        assertFalse(value.isLong());
    }

    @Test
    public void testAsLong() {
        assertEquals(Long.valueOf(1), value.asLong());
    }

    @Test
    public void testIsDouble() {
        assertFalse(value.isDouble());
    }

    @Test
    public void testAsDouble() {
        assertEquals(Double.valueOf(1), value.asDouble());
    }

    @Test
    public void testIsString() {
        assertFalse(value.isString());
    }

    @Test
    public void testAsString() {
        assertEquals("true", value.asString());
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
        assertFalse(value.isList());
    }

    @Test
    public void testAsList() {
        ValueList emptyList = value.asList();
        assertTrue(emptyList.isEmpty());
    }

    /**
     * Test av {@link BooleanValue#hashCode()}.
     */
    @Test
    public void testHashCode() {
        Value nullBooleanValue = toBooleanValue(null);
        assertEquals(1268, nullBooleanValue.hashCode());
        Value trueBooleanValue = toBooleanValue(Boolean.TRUE);
        assertEquals(1262, trueBooleanValue.hashCode());
    }

    /**
     * Test av {@link BooleanValue#equals(Object)}.
     */
    @Test
    public void testEquals() {
        Value nullBooleanValue = toBooleanValue(null);
        Value falseBooleanValue = toBooleanValue(Boolean.FALSE);
        Value trueBooleanValue = toBooleanValue(Boolean.TRUE);
        assertFalse(nullBooleanValue.equals(null));
        assertFalse(nullBooleanValue.equals(getNil()));
        assertTrue(nullBooleanValue.equals(nullBooleanValue));
        assertTrue(nullBooleanValue.equals(falseBooleanValue));
        assertFalse(nullBooleanValue.equals(trueBooleanValue));
    }

    /**
     * Test av {@link BooleanValue#toString()}.
     */
    @Test
    public void testToString() {
        Value nullBooleanValue = toBooleanValue(null);
        assertEquals("BooleanValue [value=false]", nullBooleanValue.toString());
        Value falseBooleanValue = toBooleanValue(Boolean.FALSE);
        assertEquals("BooleanValue [value=false]", falseBooleanValue.toString());
        Value trueBooleanValue = toBooleanValue(Boolean.TRUE);
        assertEquals("BooleanValue [value=true]", trueBooleanValue.toString());
    }
}
