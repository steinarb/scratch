package no.priv.bang.modeling.modelstore.value;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import no.priv.bang.modeling.modelstore.services.DateFactory;
import no.priv.bang.modeling.modelstore.services.ModificationRecorder;
import no.priv.bang.modeling.modelstore.services.Propertyset;
import no.priv.bang.modeling.modelstore.services.ValueList;

/**
 * Unit tests for {@link PropertysetRecordingSaveTime}.
 *
 */
class PropertysetRecordingSaveTimeTest {

    private final static UUID generalObjectId = UUID.fromString("06cee83c-2ca8-44b8-8035-c79586665532");
    private final static UUID propertysetId = UUID.fromString("a72f6189-f132-4714-8f11-6258967a74ce");
    private ModificationRecorder recorder;
    private ValueCreatorProvider valueCreator;
    private Propertyset inner;
    private Propertyset propertyset;
    @BeforeEach
    void setup() {
        valueCreator = new ValueCreatorProvider();
        var instant = LocalDateTime.now().toInstant(ZoneOffset.UTC);
        var dateFactory = mock(DateFactory.class);
        when(dateFactory.now())
            .thenReturn(Date.from(instant.plusMillis(1000)))
            .thenReturn(Date.from(instant.plusMillis(2000)))
            .thenReturn(Date.from(instant.plusMillis(3000)))
            .thenReturn(Date.from(instant.plusMillis(4000)))
            .thenReturn(Date.from(instant.plusMillis(5000)))
            .thenReturn(Date.from(instant.plusMillis(6000)))
            .thenReturn(Date.from(instant.plusMillis(7000)))
            .thenReturn(Date.from(instant.plusMillis(8000)))
            .thenReturn(Date.from(instant.plusMillis(9000)))
            .thenReturn(Date.from(instant.plusMillis(10000)))
            .thenReturn(Date.from(instant.plusMillis(11000)));
        recorder = new ModificationRecorder() {
                Map<UUID, Date> modificationTimes = new HashMap<>();
                DateFactory datefac = dateFactory;

                @Override
                public void modifiedPropertyset(Propertyset propertyset) {
                    modificationTimes.put(propertyset.getId(), datefac.now());
                }

                @Override
                public Date getLastmodifieddate(Propertyset propertyset) {
                    return modificationTimes.computeIfAbsent(propertyset.getId(), k -> datefac.now());
                }
            };
        inner = valueCreator.newPropertyset(propertysetId );
        propertyset = new PropertysetRecordingSaveTime(recorder, inner);
        addProperties(propertyset);
    }

    private void addProperties(Propertyset propertyset2) {
        propertyset.addAspect(valueCreator.newPropertyset(generalObjectId));
        propertyset.setBooleanProperty("a", true);
        propertyset.setLongProperty("b", 1);
        propertyset.setDoubleProperty("c", 1.1);
        propertyset.setStringProperty("d", "foo bar");
        propertyset.setComplexProperty("e", valueCreator.newPropertyset());
        propertyset.getComplexProperty("e").setBooleanProperty("aa", true);
        propertyset.getComplexProperty("e").setLongProperty("bb", 2);
        Propertyset other = valueCreator.newPropertyset(UUID.fromString("72c5cd3a-178e-4579-ace5-72d857a0d953"));
        other.addAspect(valueCreator.newPropertyset(generalObjectId));
        other.setStringProperty("cc", "bar foo");
        other.setDoubleProperty("dd", 2.1);
        propertyset.setReferenceProperty("f", other);
        propertyset.setListProperty("g", valueCreator.newValueList());
        propertyset.getListProperty("g").add(1);
        propertyset.getListProperty("g").add(2);
    }

    /**
     * Unit test of {@link PropertysetRecordingSaveTime#isNil()}.
     */
    @Test
    void testIsNil() {
        assertFalse(propertyset.isNil());
    }

    /**
     * Unit test of {@link PropertysetRecordingSaveTime#getPropertynames()}.
     */
    @Test
    void testGetPropertynames() {
        assertEquals(9, propertyset.getPropertynames().size());
    }

    /**
     * Unit test of {@link PropertysetRecordingSaveTime#getProperty()}.
     */
    @Test
    void testGetProperty() {
        assertTrue(propertyset.getProperty("a").isBoolean());
    }

    /**
     * Unit test of {@link PropertysetRecordingSaveTime#setProperty()}.
     * @throws InterruptedException
     */
    @Test
    void testSetProperty() throws InterruptedException {
        // Expected the set value to change the lastmodifiedtime of the propertyset
        Date lastmodifiedTimeBeforeSetProperty = recorder.getLastmodifieddate(propertyset);
        propertyset.setProperty("a", Values.toDoubleValue(1.7));
        Date lastmodifiedTimeAfterSetProperty = recorder.getLastmodifieddate(propertyset);
        assertNotEquals(lastmodifiedTimeBeforeSetProperty, lastmodifiedTimeAfterSetProperty);
    }

    /**
     * Unit test of {@link PropertysetRecordingSaveTime#hasAspect()}.
     */
    @Test
    void testHasAspect() {
        assertTrue(propertyset.hasAspect());
    }

    /**
     * Unit test of {@link PropertysetRecordingSaveTime#getAspects()}.
     */
    @Test
    void testGetAspects() {
        assertEquals(1, propertyset.getAspects().size());
    }

    /**
     * Unit test of {@link PropertysetRecordingSaveTime#hasId()}.
     */
    @Test
    void testHasId() {
        assertTrue(propertyset.hasId());
    }

    /**
     * Unit test of {@link PropertysetRecordingSaveTime#getId()}.
     */
    @Test
    void testGetId() {
        assertEquals(propertysetId, propertyset.getId());
    }

    /**
     * Unit test of {@link PropertysetRecordingSaveTime#setBooleanProperty(String, boolean)},
     * {@link PropertysetRecordingSaveTime#setBooleanProperty(String, Boolean)}, and
     * {@link PropertysetRecordingSaveTime#getBooleanProperty(String)}
     * @throws InterruptedException
     */
    @Test
    void testSetGetBooleanProperty() throws InterruptedException {
        // Expected the set value to change the lastmodifiedtime of the propertyset
        Date lastmodifiedTimeBeforeSetProperty = recorder.getLastmodifieddate(propertyset);
        propertyset.setBooleanProperty("a", Boolean.FALSE);
        Date lastmodifiedTimeAfterSetProperty1 = recorder.getLastmodifieddate(propertyset);
        assertNotEquals(lastmodifiedTimeBeforeSetProperty, lastmodifiedTimeAfterSetProperty1);
        assertFalse(propertyset.getBooleanProperty("a"));
        propertyset.setBooleanProperty("a", true);
        Date lastmodifiedTimeAfterSetProperty2 = recorder.getLastmodifieddate(propertyset);
        assertNotEquals(lastmodifiedTimeAfterSetProperty1, lastmodifiedTimeAfterSetProperty2);
        assertTrue(propertyset.getBooleanProperty("a"));
    }

    /**
     * Unit test of {@link PropertysetRecordingSaveTime#setLongProperty(String, long)},
     * {@link PropertysetRecordingSaveTime#setLongProperty(String, Long)}, and
     * {@link PropertysetRecordingSaveTime#getLongProperty(String)}
     * @throws InterruptedException
     */
    @Test
    void testSetGetLongProperty() throws InterruptedException {
        // Expected the set value to change the lastmodifiedtime of the propertyset
        Date lastmodifiedTimeBeforeSetProperty = recorder.getLastmodifieddate(propertyset);
        propertyset.setLongProperty("b", Long.valueOf(128));
        Date lastmodifiedTimeAfterSetProperty1 = recorder.getLastmodifieddate(propertyset);
        assertNotEquals(lastmodifiedTimeBeforeSetProperty, lastmodifiedTimeAfterSetProperty1);
        assertEquals(128, propertyset.getLongProperty("b").longValue());
        propertyset.setLongProperty("b", 127);
        Date lastmodifiedTimeAfterSetProperty2 = recorder.getLastmodifieddate(propertyset);
        assertNotEquals(lastmodifiedTimeAfterSetProperty1, lastmodifiedTimeAfterSetProperty2);
        assertEquals(127, propertyset.getLongProperty("b").longValue());
    }

    /**
     * Unit test of {@link PropertysetRecordingSaveTime#setDoubleProperty(String, double)},
     * {@link PropertysetRecordingSaveTime#setDoubleProperty(String, Double)}, and
     * {@link PropertysetRecordingSaveTime#getDoubleProperty(String)}
     * @throws InterruptedException
     */
    @Test
    void testSetGetDoubleProperty() throws InterruptedException {
        // Expected the set value to change the lastmodifiedtime of the propertyset
        Date lastmodifiedTimeBeforeSetProperty = recorder.getLastmodifieddate(propertyset);
        propertyset.setDoubleProperty("c", Double.valueOf(12.8));
        Date lastmodifiedTimeAfterSetProperty1 = recorder.getLastmodifieddate(propertyset);
        assertNotEquals(lastmodifiedTimeBeforeSetProperty, lastmodifiedTimeAfterSetProperty1);
        assertEquals(12.8, propertyset.getDoubleProperty("c").doubleValue(), 0.0);
        propertyset.setDoubleProperty("c", 1.27);
        Date lastmodifiedTimeAfterSetProperty2 = recorder.getLastmodifieddate(propertyset);
        assertNotEquals(lastmodifiedTimeAfterSetProperty1, lastmodifiedTimeAfterSetProperty2);
        assertEquals(1.27, propertyset.getDoubleProperty("c").doubleValue(), 0.0);
    }

    /**
     * Unit test of {@link PropertysetRecordingSaveTime#setStringProperty(String, String)},
     * and {@link PropertysetRecordingSaveTime#getStringProperty(String)}
     * @throws InterruptedException
     */
    @Test
    void testSetGetStringProperty() throws InterruptedException {
        // Expected the set value to change the lastmodifiedtime of the propertyset
        Date lastmodifiedTimeBeforeSetProperty = recorder.getLastmodifieddate(propertyset);
        propertyset.setStringProperty("d", "abcd");
        Date lastmodifiedTimeAfterSetProperty = recorder.getLastmodifieddate(propertyset);
        assertNotEquals(lastmodifiedTimeBeforeSetProperty, lastmodifiedTimeAfterSetProperty);
        assertEquals("abcd", propertyset.getStringProperty("d"));
    }

    /**
     * Unit test of {@link PropertysetRecordingSaveTime#setComplexProperty(String, Propertyset)},
     * and {@link PropertysetRecordingSaveTime#getComplexProperty(String)}
     * @throws InterruptedException
     */
    @Test
    void testSetGetComplexProperty() throws InterruptedException {
        // Expected the set value to change the lastmodifiedtime of the propertyset
        Date lastmodifiedTimeBeforeSetProperty = recorder.getLastmodifieddate(propertyset);
        // This is a back door: it is possible to manipulate a complex property without changing the timestamp on the propertyset
        propertyset.getComplexProperty("e").setStringProperty("cc", "modified");
        Date lastmodifiedTimeAfterSetProperty1 = recorder.getLastmodifieddate(propertyset);
        assertEquals(lastmodifiedTimeBeforeSetProperty, lastmodifiedTimeAfterSetProperty1, "Expected the time stamps to be identical");
        assertEquals("modified", propertyset.getComplexProperty("e").getStringProperty("cc"));
        Propertyset complex = propertyset.getComplexProperty("e");
        complex.setStringProperty("cc", "modified again");
        propertyset.setComplexProperty("e", complex);
        Date lastmodifiedTimeAfterSetProperty2 = recorder.getLastmodifieddate(propertyset);
        assertNotEquals(lastmodifiedTimeAfterSetProperty1, lastmodifiedTimeAfterSetProperty2, "Expected modification time to be changed");
        assertEquals("modified again", propertyset.getComplexProperty("e").getStringProperty("cc"));
    }

    /**
     * Unit test of {@link PropertysetRecordingSaveTime#setReferenceProperty(String, Propertyset)},
     * and {@link PropertysetRecordingSaveTime#getReferenceProperty(String)}
     * @throws InterruptedException
     */
    @Test
    void testSetGetReferenceProperty() throws InterruptedException {
        // Expected the set value to change the lastmodifiedtime of the propertyset
        Date lastmodifiedTimeBeforeSetProperty = recorder.getLastmodifieddate(propertyset);
        UUID newReferencedPropertysetId = UUID.randomUUID();
        propertyset.setReferenceProperty("f", valueCreator.newPropertyset(newReferencedPropertysetId));
        Date lastmodifiedTimeAfterSetProperty = recorder.getLastmodifieddate(propertyset);
        assertNotEquals(lastmodifiedTimeBeforeSetProperty, lastmodifiedTimeAfterSetProperty);
        assertEquals(newReferencedPropertysetId, propertyset.getReferenceProperty("f").getId());
    }

    /**
     * Unit test of {@link PropertysetRecordingSaveTime#setListProperty(String, Propertyset)},
     * and {@link PropertysetRecordingSaveTime#getListProperty(String)}
     * @throws InterruptedException
     */
    @Test
    void testSetGetListProperty() throws InterruptedException {
        // Expected the set value to change the lastmodifiedtime of the propertyset
        Date lastmodifiedTimeBeforeSetProperty = recorder.getLastmodifieddate(propertyset);
        // This is a back door: it is possible to manipulate a list property without changing the timestamp on the propertyset
        propertyset.getListProperty("g").add("modified");
        Date lastmodifiedTimeAfterSetProperty1 = recorder.getLastmodifieddate(propertyset);
        assertEquals(lastmodifiedTimeBeforeSetProperty, lastmodifiedTimeAfterSetProperty1, "Expected the time stamps to be identical");
        assertEquals(3, propertyset.getListProperty("g").size());
        ValueList list = propertyset.getListProperty("g");
        list.add("modified again");
        propertyset.setListProperty("g", list);
        Date lastmodifiedTimeAfterSetProperty2 = recorder.getLastmodifieddate(propertyset);
        assertNotEquals(lastmodifiedTimeAfterSetProperty1, lastmodifiedTimeAfterSetProperty2, "Expected modification time to be changed");
        assertEquals(4, propertyset.getListProperty("g").size());
    }

    /**
     * Unit test of {@link PropertysetRecordingSaveTime#hashCode()}.
     */
    @Test
    void testHashCode() {
        assertEquals(-1095503838, propertyset.hashCode());
    }

    /**
     * Unit test of {@link PropertysetRecordingSaveTime#equals(Object)}.
     */
    @Test
    void testEquals() {
        assertEquals(propertyset, propertyset);
        assertNotEquals(propertyset, null); // NOSONAR the point here is to test propertyset.equals, so no the arguments should not be swapped
        // Same underlying object, different wrapper
        var copyOfPropertyset = new PropertysetRecordingSaveTime(recorder, inner);
        assertEquals(propertyset, copyOfPropertyset);
        assertEquals(copyOfPropertyset, propertyset);

        // Same context, different propertyset
        Propertyset otherPropertyset = valueCreator.newPropertyset(UUID.randomUUID());
        assertNotEquals(propertyset, otherPropertyset);

        // Compare underlying object
        assertEquals(propertyset, inner, "Expected inner object to compare equals to wrapper");
    }

    /**
     * Unit test of {@link PropertysetRecordingSaveTime#toString()}.
     */
    @Test
    void testToString() {
        assertThat(propertyset.toString()).startsWith("PropertysetRecordingSaveTime ");
    }

    @Test
    void testMapBehaviour() {
        var valueCreator = new ValueCreatorProvider();
        var mapToCopy = Map.of("pi", valueCreator.fromDouble(3.14), "meaning", valueCreator.fromLong(42L));
        var inner = valueCreator.newPropertyset();
        var propertyset = valueCreator.wrapInModificationTracker(recorder, inner);
        assertThat(propertyset).isEmpty();
        propertyset.putAll(mapToCopy);
        assertThat(propertyset).hasSize(mapToCopy.size());
        assertThat(propertyset.keySet()).containsExactlyInAnyOrderElementsOf(mapToCopy.keySet());
        assertThat(propertyset.entrySet()).hasSize(mapToCopy.entrySet().size());
        assertThat(propertyset.values()).containsExactlyInAnyOrderElementsOf(mapToCopy.values());
        assertTrue(propertyset.containsKey("pi"));
        assertTrue(propertyset.containsValue(new LongValue(42L)));
        var barValue = valueCreator.fromString("bar");
        propertyset.put("foo", barValue);
        assertThat(propertyset).hasSizeGreaterThan(mapToCopy.size());
        assertEquals(barValue, propertyset.get("foo"));
        assertEquals(barValue, propertyset.remove("foo"));
        assertEquals(valueCreator.getNil(), propertyset.remove("foo"));
        assertThat(propertyset).hasSize(mapToCopy.size());
        propertyset.clear();
        assertThat(propertyset).isEmpty();
    }
}
