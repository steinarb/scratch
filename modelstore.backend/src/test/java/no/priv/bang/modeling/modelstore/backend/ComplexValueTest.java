package no.priv.bang.modeling.modelstore.backend;

import static no.priv.bang.modeling.modelstore.backend.Values.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import no.priv.bang.modeling.modelstore.services.Propertyset;
import no.priv.bang.modeling.modelstore.services.Value;
import no.priv.bang.modeling.modelstore.services.ValueList;

/**
 * Unit tests for {@link ComplexValue}.
 *
 * @author Steinar Bang
 *
 */
class ComplexValueTest {

    private Propertyset complexProperty;
    private Value value;

    @BeforeEach
    void setUp() throws Exception {
        complexProperty = new PropertysetImpl();
        complexProperty.setBooleanProperty("boolean", Boolean.TRUE);
        complexProperty.setLongProperty("long", Long.valueOf(42));
        complexProperty.setDoubleProperty("double", Double.valueOf(2.78));
        complexProperty.setStringProperty("string", "foo bar");
        value = toComplexValue(complexProperty, false);
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
        assertTrue(value.isComplexProperty());
    }

    @Test
    void testAsComplexProperty() {
        assertEquals(complexProperty, value.asComplexProperty());
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
     * Test av {@link ComplexValue#hashCode()}.
     */
    @Test
    void testHashCode() {
        Value nullComplexValue = toComplexValue(null);
        assertEquals(31, nullComplexValue.hashCode());
        assertEquals(1958288831, value.hashCode());
    }

    /**
     * Test av {@link ComplexValue#equals(Object)}.
     */
    @Test
    void testEquals() {
        Value nullComplexValue = toComplexValue(null);
        assertFalse(nullComplexValue.equals(null));
        assertFalse(nullComplexValue.equals(getNil().asComplexProperty()));
        assertTrue(nullComplexValue.equals(nullComplexValue));
        assertFalse(nullComplexValue.equals(value));
        assertFalse(value.equals(nullComplexValue));
        assertTrue(value.equals(value));
    }

    /**
     * Test av {@link ComplexValue#toString()}.
     */
    @Test
    void testToString() {
        Value nullComplexValue = toComplexValue(null);
        assertEquals("ComplexValue [value=PropertysetNil []]", nullComplexValue.toString());
        assertEquals("ComplexValue [value=PropertysetImpl [properties={boolean=BooleanValue [value=true], string=StringValue [value=foo bar], double=DoubleValue [value=2.78], long=LongValue [value=42]}]]", value.toString());
    }

}
