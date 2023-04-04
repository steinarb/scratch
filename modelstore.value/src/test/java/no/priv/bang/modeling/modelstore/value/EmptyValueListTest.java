package no.priv.bang.modeling.modelstore.value;

import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static no.priv.bang.modeling.modelstore.value.Values.*;
import static org.junit.jupiter.api.Assertions.*;

import no.priv.bang.modeling.modelstore.services.Value;
import no.priv.bang.modeling.modelstore.services.ValueList;

/**
 * Unit tests for {@link EmptyValueList}.
 *
 */
class EmptyValueListTest {

    private ValueList list;

    @BeforeEach
    void setUp() throws Exception {
        list = new EmptyValueList();
    }

    @Test
    void testAdd() {
        assertFalse(list.add((Value)null));
        assertEquals(0, list.size());
        list.add(1024, null);
        assertEquals(0, list.size());
        list.add(Boolean.TRUE);
        assertEquals(0, list.size());
        list.add(true);
        assertEquals(0, list.size());
        list.add(Long.valueOf(3));
        assertEquals(0, list.size());
        list.add(37);
        assertEquals(0, list.size());
        list.add(Double.valueOf(3.14));
        assertEquals(0, list.size());
        list.add(2.78);
        assertEquals(0, list.size());
        list.add("foo");
        assertEquals(0, list.size());
        list.add(new PropertysetImpl());
        assertEquals(0, list.size());
        list.add(newList());
        assertEquals(0, list.size());
    }

    @Test
    void testAddAll() {
        assertFalse(list.addAll(new EmptyValueList()));
        assertFalse(list.addAll(0, new EmptyValueList()));
    }

    @Test
    void testContains() {
        assertFalse(list.contains(null));
    }

    @Test
    void testContainsAll() {
        assertTrue(list.containsAll(new EmptyValueList()));
    }

    @Test
    void testGet() {
        assertEquals(getNil(), list.get(135));
    }

    @Test
    void testIndexOf() {
        assertEquals(-1, list.indexOf(null));
    }

    @Test
    void testIsEmpty() {
        assertTrue(list.isEmpty());
    }

    @Test
    void testIterator() {
        assertNotNull(list.iterator());
    }

    @Test
    void testLastIndexOf() {
        assertEquals(-1, list.lastIndexOf(null));
    }

    @Test
    void testListIterator() {
        assertNotNull(list.listIterator());
    }

    @Test
    void testListIteratorWithArg() {
        assertThrows(IndexOutOfBoundsException.class, () -> list.listIterator(135));
    }

    @Test
    void testRemove() {
        assertFalse(list.remove(null));
        assertEquals(getNil(), list.remove(135));
    }

    @Test
    void testRemoveAll() {
        assertFalse(list.removeAll(Collections.emptyList()));
    }

    @Test
    void testRetainAll() {
        assertFalse(list.retainAll(Collections.emptyList()));
    }

    @Test
    void testSet() {
        assertEquals(getNil(), list.set(135, (Value)null));
        assertEquals(getNil(), list.set(135, Boolean.TRUE));
        assertEquals(getNil(), list.set(135, true));
        assertEquals(getNil(), list.set(135, Long.valueOf(1)));
        assertEquals(getNil(), list.set(135, 2));
        assertEquals(getNil(), list.set(135, Double.valueOf(1.1)));
        assertEquals(getNil(), list.set(135, 2.4));
        assertEquals(getNil(), list.set(135, "foobar"));
        assertEquals(getNil(), list.set(135, new PropertysetImpl()));
        assertEquals(getNil(), list.set(135, newList()));
    }

    @Test
    void testSize() {
        assertEquals(0, list.size());
    }

    @Test
    void testSubList() {
        assertThrows(IndexOutOfBoundsException.class, () -> list.subList(1, 2));
    }

    @Test
    void testToArray() {
        Object[] array1 = list.toArray();
        assertEquals(0, array1.length);
        Value[] valueArray = new Value[10];
        Value[] array2 = list.toArray(valueArray);
        assertEquals(0, array2.length);
    }

}
