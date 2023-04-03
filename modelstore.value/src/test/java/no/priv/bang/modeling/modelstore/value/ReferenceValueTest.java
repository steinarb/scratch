package no.priv.bang.modeling.modelstore.backend;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static no.priv.bang.modeling.modelstore.backend.Values.*;
import static org.junit.jupiter.api.Assertions.*;

import no.priv.bang.modeling.modelstore.services.Propertyset;
import no.priv.bang.modeling.modelstore.services.Value;
import no.priv.bang.modeling.modelstore.services.ValueList;

/**
 * Unit tests for {@link ReferenceValue}.
 *
 */
class ReferenceValueTest {

    private Propertyset referencedObject;
    private Value value;

    @BeforeEach
    void setUp() throws Exception {
        referencedObject = new PropertysetImpl(UUID.fromString("276dbd6e-dc46-4c14-af9e-83c63c10e0b3"));
        referencedObject.setBooleanProperty("boolean", Boolean.TRUE);
        referencedObject.setLongProperty("long", Long.valueOf(42));
        referencedObject.setDoubleProperty("double", Double.valueOf(2.78));
        referencedObject.setStringProperty("string", "foo bar");
        value = toReferenceValue(referencedObject);
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
        assertTrue(value.isReference());
    }

    @Test
    void testAsReference() {
        assertEquals(referencedObject, value.asReference());
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
     * Test av {@link ReferenceValue#hashCode()}.
     */
    @Test
    void testHashCode() {
        Value nullReferenceValue = toReferenceValue(null);
        assertEquals(31, nullReferenceValue.hashCode());
        assertEquals(1755681326, value.hashCode());
    }

    /**
     * Test av {@link ReferenceValue#equals(Object)}.
     */
    @Test
    void testEquals() {
        Value nullReferenceValue = toReferenceValue(null);
        assertNotEquals(nullReferenceValue, null); // NOSONAR the point here is to test propertyset.equals, so no the arguments should not be swapped
        assertNotEquals(nullReferenceValue,getNil().asComplexProperty());
        assertEquals(nullReferenceValue, nullReferenceValue);
        assertNotEquals(nullReferenceValue, value);
        assertNotEquals(value, nullReferenceValue);
        assertEquals(value, value);

        // Compare two different object with no id property
        Value refToNoId = toReferenceValue(new PropertysetImpl());
        Value refToNoId2 = toReferenceValue(new PropertysetImpl());
        assertEquals(refToNoId, refToNoId2);

    }

    /**
     * Test av {@link ReferenceValue#toString()}.
     */
    @Test
    void testToString() {
        Value nullReferenceValue = toReferenceValue(null);
        assertEquals("ReferenceValue [value=00000000-0000-0000-0000-000000000000]", nullReferenceValue.toString());
        assertEquals("ReferenceValue [value=276dbd6e-dc46-4c14-af9e-83c63c10e0b3]", value.toString());
    }

}
