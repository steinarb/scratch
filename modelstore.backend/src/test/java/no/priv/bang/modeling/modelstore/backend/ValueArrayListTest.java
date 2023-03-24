package no.priv.bang.modeling.modelstore.backend;

import static no.priv.bang.modeling.modelstore.backend.Values.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import no.priv.bang.modeling.modelstore.services.ModelContext;
import no.priv.bang.modeling.modelstore.services.Propertyset;
import no.priv.bang.modeling.modelstore.services.Value;
import no.priv.bang.modeling.modelstore.services.ValueList;

/**
 * Unit tests for {@link ValueArrayList}.
 *
 * @author Steinar Bang
 *
 */
public class ValueArrayListTest {
    /**
     * Test of basic list operations.
     */
    @Test
    public void testAddGetPutRemove() {
        ModelContext context = new ModelstoreProvider().getDefaultContext();
        ValueList list = newList();
        assertEquals(0, list.size());
        list.add(toStringValue("a"));
        list.add(toLongValue(4L));
        Propertyset propertyset = context.createPropertyset();
        propertyset.setStringProperty("a", "foo bar");
        list.add(propertyset);
        ValueList listelement = newList();
        listelement.add("foo");
        list.add(listelement);
        assertEquals(4, list.size());
        assertEquals(4L, list.get(1).asLong().longValue());
        list.set(1, toLongValue(3L));
        list.remove(0);
        assertEquals(3, list.size());
        assertEquals(3L, list.get(0).asLong().longValue());
        list.add((Value)null);
        assertEquals(getNilPropertyset(), list.get(3).asComplexProperty());
        list.set(3, (Value)null);
        assertEquals(getNilPropertyset(), list.get(3).asComplexProperty());

        // Verify deep copy for Propertysets and lists
        ValueList addlist = newList();
        addlist.add(list.get(1)); // Complex value
        addlist.add(list.get(2));
        addlist.get(0).asComplexProperty().setStringProperty("a", "bar foo");
        assertEquals("bar foo", addlist.get(0).asComplexProperty().getStringProperty("a"));
        assertEquals("foo bar", list.get(1).asComplexProperty().getStringProperty("a"), "Expected original to be unchanged");
        addlist.get(1).asList().add("bar");
        assertEquals(2, addlist.get(1).asList().size());
        assertEquals(1, list.get(2).asList().size(), "Expected original to be unchanged");
        ValueList setlist = newList();
        setlist.add(true);
        setlist.add(true); // Just add something to be able to set index 0 and 1
        setlist.set(0, list.get(1));
        setlist.set(1, list.get(2));
        setlist.get(0).asComplexProperty().setStringProperty("a", "foobar");
        assertEquals("foobar", setlist.get(0).asComplexProperty().getStringProperty("a"));
        assertEquals("foo bar", list.get(1).asComplexProperty().getStringProperty("a"), "Expected original to be unchanged");
        setlist.get(1).asList().add("bar");
        assertEquals(2, setlist.get(1).asList().size());
        assertEquals(1, list.get(2).asList().size(), "Expected original to be unchanged");
    }

    /**
     * Test of basic list operations for boolean values.
     */
    @Test
    public void testAddSetGetBoolean() {
        ValueList list = newList();
        assertEquals(0, list.size());
        list.add(Boolean.TRUE);
        list.add(true);
        assertTrue(list.get(0).asBoolean());
        assertTrue(list.get(1).asBoolean());
        list.set(0, false);
        list.set(1, Boolean.FALSE);
        assertFalse(list.get(0).asBoolean());
        assertFalse(list.get(1).asBoolean());
    }

    /**
     * Test of basic list operations for long values.
     */
    @Test
    public void testAddSetGetLong() {
        ValueList list = newList();
        assertEquals(0, list.size());
        list.add(Long.valueOf(1));
        list.add(2L);
        assertEquals(1, list.get(0).asLong().longValue());
        assertEquals(2, list.get(1).asLong().longValue());
        list.set(0, 3L);
        list.set(1, Long.valueOf(4));
        assertEquals(3, list.get(0).asLong().longValue());
        assertEquals(4, list.get(1).asLong().longValue());
    }

    /**
     * Test of basic list operations for double values.
     */
    @Test
    public void testAddSetGetDouble() {
        ValueList list = newList();
        assertEquals(0, list.size());
        list.add(Double.valueOf(1.0));
        list.add(2.0);
        assertEquals(1.0, list.get(0).asDouble().doubleValue(), 0.0);
        assertEquals(2.0, list.get(1).asDouble().doubleValue(), 0.0);
        list.set(0, 3.0);
        list.set(1, Double.valueOf(4));
        assertEquals(3.0, list.get(0).asDouble().doubleValue(), 0.0);
        assertEquals(4.0, list.get(1).asDouble().doubleValue(), 0.0);
    }

    /**
     * Test of basic list operations for string values.
     */
    @Test
    public void testAddSetGetString() {
        ValueList list = newList();
        assertEquals(0, list.size());
        list.add("foo");
        list.add("bar");
        assertEquals("foo", list.get(0).asString());
        assertEquals("bar", list.get(1).asString());
        list.set(1, "foobar");
        assertEquals("foobar", list.get(1).asString());
    }

    /**
     * Test of basic list operations for {@link Propertyset} values.
     *
     * A {@link Propertyset} value where {@link Propertyset#hasId()} is
     * true, will become a {@link ReferenceValue}, if hasId is
     * false, it will become a {@link ComplexValue}.
     *
     * A null argument will become whatever {@link Values#toComplexValue(Propertyset)}
     * does with a null.
     */
    @Test
    public void testAddSetGetPropertyset() {
        ModelContext modelContext = new ModelstoreProvider().getDefaultContext();
        Propertyset objectWithoutId = modelContext.createPropertyset();
        objectWithoutId.setDoubleProperty("c", 3.14);
        UUID id = UUID.randomUUID();
        Propertyset objectWithId = modelContext.findPropertyset(id);
        objectWithId.setLongProperty("a", Long.valueOf(2));
        objectWithId.setStringProperty("b", "foo bar");
        Propertyset nullObject = null;
        ValueList list = newList();
        assertEquals(0, list.size());
        list.add(objectWithoutId);
        list.add(objectWithId);
        list.add(nullObject);
        assertTrue(list.get(0).isComplexProperty());
        assertTrue(list.get(1).isReference());
        assertTrue(list.get(2).isComplexProperty());
        list.set(0, nullObject);
        list.set(1, objectWithoutId);
        list.set(2, objectWithId);
        assertTrue(list.get(0).isComplexProperty());
        assertTrue(list.get(1).isComplexProperty());
        assertTrue(list.get(2).isReference());

        // Verify deep copy of propertysets in add and set
        ValueList otherlist = newList();
        otherlist.add(list.get(1).asComplexProperty());
        otherlist.get(0).asComplexProperty().setDoubleProperty("c", 3.78);
        assertEquals(3.78, otherlist.get(0).asComplexProperty().getDoubleProperty("c"), 0.0);
        assertEquals(3.14, list.get(1).asComplexProperty().getDoubleProperty("c"), 0.0, "Expected original value to be unchanged");
        ValueList otherlist2 = newList();
        otherlist2.add(true); // Dummy add to get a settable position in the list
        otherlist2.set(0, list.get(1).asComplexProperty());
        otherlist2.get(0).asComplexProperty().setDoubleProperty("c", 2.78);
        assertEquals(2.78, otherlist2.get(0).asComplexProperty().getDoubleProperty("c"), 0.0);
        assertEquals(3.14, list.get(1).asComplexProperty().getDoubleProperty("c"), 0.0, "Expected original value to be unchanged");
    }

    /**
     * Test of basic list operations for {@link ValueList} values.
     *
     * A null argument will become whatever {@link Values#toListValue(ValueList)}
     * does with a null.
     */
    @Test
    public void testAddSetGetValueList() {
        ValueList value1 = newList();
        value1.add(3.14);
        value1.add(2.78);
        value1.add("foo");
        ValueList value2 = newList();
        value2.add(true);
        value2.add(false);
        ValueList nullList = null;

        // Create the list and insert lists into the list
        ValueList list = newList();
        assertEquals(0, list.size());
        list.add(value1);
        list.add(value2);
        list.add(nullList);
        assertEquals(3, list.size());
        assertEquals(3, list.get(0).asList().size());
        assertEquals(2, list.get(1).asList().size());
        assertEquals(0, list.get(2).asList().size());
        list.set(0, nullList);
        list.set(1, value1);
        list.set(2, value2);
        assertEquals(0, list.get(0).asList().size());
        assertEquals(3, list.get(1).asList().size());
        assertEquals(2, list.get(2).asList().size());

        // Verify deep copy of lists in add and set
        ValueList otherlist = newList();
        otherlist.add(list.get(1).asList());
        otherlist.add(true); // Dummy add to get a settable position in the list
        otherlist.set(1, list.get(2).asList());
        otherlist.get(0).asList().add(3.7);
        assertEquals(4, otherlist.get(0).asList().size());
        assertEquals(3, list.get(1).asList().size(), "Expected the original to be unchanged");
        otherlist.get(1).asList().add(54);
        assertEquals(3, otherlist.get(1).asList().size());
        assertEquals(2, list.get(2).asList().size(), "Expected the original to be unchanged");
    }

    /**
     * Test av {@link ValueArrayList#equals(Object)}.
     */
    @Test
    public void testCopyConstructor() {
        ModelContext context = new ModelstoreProvider().getDefaultContext();
        UUID id = UUID.randomUUID();
        ValueList original = newList();
        populateList(original, context, id);

        ValueList copy = new ValueArrayList(original);
        assertNotSame(original, copy); // Obviously...
        assertEquals(original, copy);

        // Modify elements in the copy and verify that there is no effect on the original
        copy.set(0, false);
        assertFalse(copy.get(0).asBoolean());
        assertTrue(original.get(0).asBoolean(), "Expected original to be unchanged");
        copy.set(1, 43);
        assertEquals(43, copy.get(1).asLong().longValue());
        assertEquals(42, original.get(1).asLong().longValue(), "Expected original to be unchanged");
        copy.set(2, 2.78);
        assertEquals(2.78, copy.get(2).asDouble(), 0.0);
        assertEquals(2.7, original.get(2).asDouble(), 0.0, "Expected original to be unchanged");
        copy.set(3, "bar foo");
        assertEquals("bar foo", copy.get(3).asString());
        Propertyset originalReference = copy.get(4).asReference();
        Propertyset newReference = context.findPropertyset(UUID.randomUUID());
        copy.set(4, newReference);
        assertEquals(newReference, copy.get(4).asReference());
        assertEquals(originalReference, original.get(4).asReference(), "Expected original to be unchanged");
        copy.get(5).asComplexProperty().setStringProperty("d", "foobar");
        assertEquals("foobar", copy.get(5).asComplexProperty().getStringProperty("d"));
        assertEquals("bar foo", original.get(5).asComplexProperty().getStringProperty("d"), "Expected original to be unchanged");
        copy.get(6).asList().add(3);
        assertEquals(2, copy.get(6).asList().size());
        assertEquals(1, original.get(6).asList().size(), "Expected original to be unchanged");
    }

    private void populateList(ValueList list, ModelContext context, UUID id) {
        list.add(true);
        list.add(42);
        list.add(2.7);
        list.add("foo bar");
        list.add(context.findPropertyset(id));
        Propertyset propertyset = context.createPropertyset();
        propertyset.setBooleanProperty("a", true);
        propertyset.setLongProperty("b", 47);
        propertyset.setDoubleProperty("c", 3.14);
        propertyset.setStringProperty("d", "bar foo");
        list.add(propertyset);
        ValueList listAsElement = newList();
        listAsElement.add("foo");
        list.add(listAsElement);
    }

    /**
     * Test av {@link ValueArrayList#hashCode()}.
     */
    @Test
    public void testHashCode() {
        ValueList list = newList();
        assertEquals(32, list.hashCode());
        list.add(toLongValue(1L));
        assertEquals(2016, list.hashCode());
    }

    /**
     * Test av {@link ValueArrayList#equals(Object)}.
     */
    @Test
    public void testEquals() {
        ValueList list = newList();
        assertTrue(list.equals(list));
        assertFalse(list.equals(null));
        ValueList emptylist = newList();
        assertTrue(list.equals(emptylist));
        assertFalse(list.equals(getNil().asList()));
        assertFalse(list.equals(new EmptyValueList()));
        list.add(toLongValue(1L));
        assertFalse(list.equals(emptylist));
        ValueList otherlistWithSameItem = newList();
        otherlistWithSameItem.add(toLongValue(1L));
        assertTrue(list.equals(otherlistWithSameItem));
    }
}
