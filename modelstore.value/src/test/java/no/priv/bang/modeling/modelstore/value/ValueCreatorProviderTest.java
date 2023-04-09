package no.priv.bang.modeling.modelstore.value;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import no.priv.bang.modeling.modelstore.services.ModificationRecorder;

class ValueCreatorProviderTest {

    @Test
    void testGetNilPropertyset() {
        var provider = new ValueCreatorProvider();
        var value = provider.getNilPropertyset();
        assertEquals(value, value); // NOSONAR the point is to test ValueCreatorProvider.equals(this)
        assertNotEquals(value, null); // NOSONAR the point is to test ValueCreatorProvider.equals(null)
    }

    @Test
    void testGetNil() {
        var provider = new ValueCreatorProvider();
        var value = provider.getNil();
        assertEquals(value, value); // NOSONAR the point is to test ValueCreatorProvider.equals(this)
        assertNotEquals(value, null); // NOSONAR the point is to test ValueCreatorProvider.equals(null)
    }

    @Test
    void testGetBoolean() {
        var provider = new ValueCreatorProvider();
        var value = provider.fromBoolean(true);
        assertEquals(value, value); // NOSONAR the point is to test ValueCreatorProvider.equals(this)
        assertNotEquals(value, null); // NOSONAR the point is to test ValueCreatorProvider.equals(null)
        assertTrue(value.isBoolean());
        assertTrue(value.asBoolean());
        assertEquals(1, value.asLong());
        assertEquals(1, value.asDouble());
        assertEquals("true", value.asString());
        assertThat(value.asList()).isEmpty();
    }

    @Test
    void testGetLong() {
        var provider = new ValueCreatorProvider();
        var value = provider.fromLong(42L);
        assertEquals(value, value); // NOSONAR the point is to test ValueCreatorProvider.equals(this)
        assertNotEquals(value, null); // NOSONAR the point is to test ValueCreatorProvider.equals(null)
        assertFalse(value.isBoolean());
        assertTrue(value.isLong());
        assertEquals(42L, value.asLong());
        assertEquals(42L, value.asDouble());
        assertEquals("42", value.asString());
        assertThat(value.asList()).isEmpty();
    }

    @Test
    void testGetDouble() {
        var provider = new ValueCreatorProvider();
        var value = provider.fromDouble(3.14);
        assertEquals(value, value); // NOSONAR the point is to test ValueCreatorProvider.equals(this)
        assertNotEquals(value, null); // NOSONAR the point is to test ValueCreatorProvider.equals(null)
        assertFalse(value.isBoolean());
        assertFalse(value.isLong());
        assertTrue(value.isDouble());
        assertEquals(3, value.asLong());
        assertEquals(3.14, value.asDouble());
        assertThat(value.asList()).isEmpty();
    }

    @Test
    void testGetString() {
        var provider = new ValueCreatorProvider();
        var value = provider.fromString("foo");
        assertEquals(value, value); // NOSONAR the point is to test ValueCreatorProvider.equals(this)
        assertNotEquals(value, null); // NOSONAR the point is to test ValueCreatorProvider.equals(null)
        assertFalse(value.isBoolean());
        assertFalse(value.isLong());
        assertFalse(value.isDouble());
        assertTrue(value.isString());
        assertEquals(0, value.asLong());
        assertEquals(0, value.asDouble());
        assertEquals("foo", value.asString());
        assertThat(value.asList()).isEmpty();
    }

    @Test
    void testNewPropertyset() {
        var provider = new ValueCreatorProvider();
        var id = provider.getNil().asId();
        var propertyset = provider.newPropertyset();
        assertEquals(propertyset, propertyset); // NOSONAR the point is to test ValueCreatorProvider.equals(this)
        assertNotEquals(propertyset, null); // NOSONAR the point is to test ValueCreatorProvider.equals(null)
        assertEquals(id, propertyset.getId());
        assertThat(propertyset).isEmpty(); // No ID so is empty
        propertyset.setStringProperty("foo", "bar");
        propertyset.setLongProperty("meaning", 42L);
        propertyset.setDoubleProperty("pi", 3.14);
        assertEquals("bar", propertyset.getStringProperty("foo"));
        assertEquals("42", propertyset.getStringProperty("meaning"));
        assertEquals("3.14", propertyset.getStringProperty("pi"));
    }

    @Test
    void testNewPropertysetWithId() {
        var provider = new ValueCreatorProvider();
        var id = UUID.randomUUID();
        var propertyset = provider.newPropertyset(id);
        assertEquals(propertyset, propertyset); // NOSONAR the point is to test ValueCreatorProvider.equals(this)
        assertNotEquals(propertyset, null); // NOSONAR the point is to test ValueCreatorProvider.equals(null)
        assertEquals(id, propertyset.getId());
        assertThat(propertyset).isNotEmpty(); // The ID keeps the set from being empty
        propertyset.setStringProperty("foo", "bar");
        propertyset.setLongProperty("meaning", 42L);
        propertyset.setDoubleProperty("pi", 3.14);
        assertEquals("bar", propertyset.getStringProperty("foo"));
        assertEquals("42", propertyset.getStringProperty("meaning"));
        assertEquals("3.14", propertyset.getStringProperty("pi"));
    }

    @Test
    void wrapAndUnWrap() {
        var provider = new ValueCreatorProvider();
        var inner = provider.newPropertyset(UUID.randomUUID());
        var recorder = mock(ModificationRecorder.class);

        // First verify that modifying the inner directly is not recorded
        inner.setStringProperty("foo", "bar");
        assertEquals("bar", inner.getStringProperty("foo"));
        verify(recorder, never()).modifiedPropertyset(any());

        // Wrap the inner in a recording propertyset, do a change and verify that it is recorded
        var wrapped = provider.wrapInModificationTracker(recorder, inner);
        wrapped.setStringProperty("foo", "foobar");
        verify(recorder, atLeast(1)).modifiedPropertyset(any());

        // Verify that the inner propertyset has been modified
        assertEquals("foobar", inner.getStringProperty("foo"));

        // Get the unwrapped inner and verify that modifying it doesn't register anything
        var unwrapped = provider.unwrapPropertyset(wrapped);
        unwrapped.setStringProperty("foo", "barfoo");
        verify(recorder, atLeast(1)).modifiedPropertyset(any()); // Invocation count is still 1

        // Verify that inner and unwrapped are the same
        assertEquals(inner, unwrapped);
    }

    @Test
    void testUnWrapOfNonWrapped() {
        var provider = new ValueCreatorProvider();
        var inner = provider.newPropertyset(UUID.randomUUID());
        var unwrapped = provider.unwrapPropertyset(inner);
        assertSame(inner, unwrapped);
    }

    @Test
    void testGetReferenceValueFromPropertyset() {
        var provider = new ValueCreatorProvider();

        var propertyset = provider.newPropertyset(UUID.randomUUID());
        var referenceValue = provider.toReferenceValue(propertyset);
        assertTrue(referenceValue.isReference());
        assertEquals(propertyset.getId(), referenceValue.asReference().getId());
    }

    @Test
    void testGetComplexValueFromPropertyset() {
        var provider = new ValueCreatorProvider();

        var propertyset = provider.newPropertyset(UUID.randomUUID());
        var complexValue = provider.toComplexValue(propertyset);
        assertTrue(complexValue.isComplexProperty());
        assertEquals(propertyset.getId(), complexValue.asComplexProperty().getId());
    }

}
