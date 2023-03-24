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
public class ComplexValueTest {

    private Propertyset complexProperty;
    private Value value;

    @BeforeEach
    public void setUp() throws Exception {
        complexProperty = new PropertysetImpl();
        complexProperty.setBooleanProperty("boolean", Boolean.TRUE);
        complexProperty.setLongProperty("long", Long.valueOf(42));
        complexProperty.setDoubleProperty("double", Double.valueOf(2.78));
        complexProperty.setStringProperty("string", "foo bar");
        value = toComplexValue(complexProperty, false);
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
        assertTrue(value.isComplexProperty());
    }

    @Test
    public void testAsComplexProperty() {
        assertEquals(complexProperty, value.asComplexProperty());
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
     * Test av {@link ComplexValue#hashCode()}.
     */
    @Test
    public void testHashCode() {
        Value nullComplexValue = toComplexValue(null);
        assertEquals(31, nullComplexValue.hashCode());
        assertEquals(1958288831, value.hashCode());
    }

    /**
     * Test av {@link ComplexValue#equals(Object)}.
     */
    @Test
    public void testEquals() {
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
    public void testToString() {
        Value nullComplexValue = toComplexValue(null);
        assertEquals("ComplexValue [value=PropertysetNil []]", nullComplexValue.toString());
        assertEquals("ComplexValue [value=PropertysetImpl [properties={boolean=BooleanValue [value=true], string=StringValue [value=foo bar], double=DoubleValue [value=2.78], long=LongValue [value=42]}]]", value.toString());
    }

}
