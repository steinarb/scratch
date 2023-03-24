package no.priv.bang.modeling.modelstore.backend;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static no.priv.bang.modeling.modelstore.backend.Values.*;
import static org.junit.jupiter.api.Assertions.*;

import no.priv.bang.modeling.modelstore.services.Value;
import no.priv.bang.modeling.modelstore.services.ValueList;

/**
 * Unit tests for {@link IdValue}.
 *
 * @author Steinar Bang
 *
 */
public class IdValueTest {

    private Value value;

    @BeforeEach
    public void setUp() throws Exception {
        value = new IdValue(UUID.fromString("e40fb164-3dd3-43b8-839f-8781bbcb2a15"));
    }

    @Test
    public void testIsId() {
        assertTrue(value.isId());
    }

    @Test
    public void testAsId() {
        assertNotEquals(getNil().asId(), value.asId());
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
        assertEquals(Double.valueOf(0), value.asDouble());
    }

    @Test
    public void testIsString() {
        assertFalse(value.isString());
    }

    @Test
    public void testAsString() {
        assertEquals(value.asId().toString(), value.asString());
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
     * Test av {@link IdValue#hashCode()}.
     */
    @Test
    public void testHashCode() {
        IdValue nullIdValue = new IdValue(null);
        assertEquals(31, nullIdValue.hashCode());
        assertEquals(-511156377, value.hashCode());
    }

    /**
     * Test av {@link IdValue#equals(Object)}.
     */
    @Test
    public void testEquals() {
        IdValue nullIdValue = new IdValue(null);
        assertFalse(nullIdValue.equals(null));
        assertFalse(nullIdValue.equals(getNil().asId()));
        assertTrue(nullIdValue.equals(nullIdValue));
        assertFalse(nullIdValue.equals(value));
        assertFalse(value.equals(nullIdValue));
        assertTrue(value.equals(value));

        // Different object with the same UUID compares as equal
        IdValue value2 = new IdValue(UUID.fromString(value.asString()));
        assertTrue(value.equals(value2));
    }

    /**
     * Test av {@link IdValue#toString()}.
     */
    @Test
    public void testToString() {
        assertEquals("IdValue [value=e40fb164-3dd3-43b8-839f-8781bbcb2a15]", value.toString());
    }

}
