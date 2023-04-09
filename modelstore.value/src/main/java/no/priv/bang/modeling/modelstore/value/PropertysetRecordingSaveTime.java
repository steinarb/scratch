package no.priv.bang.modeling.modelstore.value;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import no.priv.bang.modeling.modelstore.services.ModificationRecorder;
import no.priv.bang.modeling.modelstore.services.Propertyset;
import no.priv.bang.modeling.modelstore.services.Value;
import no.priv.bang.modeling.modelstore.services.ValueList;

/**
 * This is an implementation of {@link Propertyset} that wraps a
 * {@link PropertysetImpl} object, and has a back reference to
 * the ModelContextRecordingMetadata that is used
 * to set the lastmodifiedtime of the {@link Propertyset}.
 *
 */
class PropertysetRecordingSaveTime implements Propertyset {

    private ModificationRecorder recorder;
    private Propertyset propertyset;

    public PropertysetRecordingSaveTime(ModificationRecorder recorder, Propertyset propertyset) {
        this.recorder = recorder;
        this.propertyset = propertyset;
    }

    public void copyValues(Propertyset propertyset) {
        this.propertyset.copyValues(propertyset);
    }

    Propertyset getPropertyset() {
        return propertyset;
    }

    public boolean isNil() {
        return propertyset.isNil();
    }

    public Collection<String> getPropertynames() {
        return propertyset.getPropertynames();
    }

    public Value getProperty(String propertyname) {
        return propertyset.getProperty(propertyname);
    }

    public void setProperty(String propertyname, Value property) {
        propertyset.setProperty(propertyname, property);
        recorder.modifiedPropertyset(propertyset);
    }

    public void addAspect(Propertyset aspect) {
        propertyset.addAspect(aspect);
    }

    public boolean hasAspect() {
        return propertyset.hasAspect();
    }

    public ValueList getAspects() {
        return propertyset.getAspects();
    }

    public boolean hasId() {
        return propertyset.hasId();
    }

    public UUID getId() {
        return propertyset.getId();
    }

    public Boolean getBooleanProperty(String propertyname) {
        return propertyset.getBooleanProperty(propertyname);
    }

    public void setBooleanProperty(String propertyname, Boolean boolValue) {
        propertyset.setBooleanProperty(propertyname, boolValue);
        recorder.modifiedPropertyset(propertyset);
    }

    public void setBooleanProperty(String propertyname, boolean boolValue) {
        propertyset.setBooleanProperty(propertyname, boolValue);
        recorder.modifiedPropertyset(propertyset);
    }

    public Long getLongProperty(String propertyname) {
        return propertyset.getLongProperty(propertyname);
    }

    public void setLongProperty(String propertyname, Long intValue) {
        propertyset.setLongProperty(propertyname, intValue);
        recorder.modifiedPropertyset(propertyset);
    }

    public void setLongProperty(String propertyname, long intvalue) {
        propertyset.setLongProperty(propertyname, intvalue);
        recorder.modifiedPropertyset(propertyset);
    }

    public Double getDoubleProperty(String propertyname) {
        return propertyset.getDoubleProperty(propertyname);
    }

    public void setDoubleProperty(String propertyname, Double doubleValue) {
        propertyset.setDoubleProperty(propertyname, doubleValue);
        recorder.modifiedPropertyset(propertyset);
    }

    public void setDoubleProperty(String propertyname, double doubleValue) {
        propertyset.setDoubleProperty(propertyname, doubleValue);
        recorder.modifiedPropertyset(propertyset);
    }

    public String getStringProperty(String propertyname) {
        return propertyset.getStringProperty(propertyname);
    }

    public void setStringProperty(String propertyname, String stringValue) {
        propertyset.setStringProperty(propertyname, stringValue);
        recorder.modifiedPropertyset(propertyset);
    }

    public Propertyset getComplexProperty(String propertyname) {
        return propertyset.getComplexProperty(propertyname);
    }

    public void setComplexProperty(String propertyname, Propertyset complexProperty) {
        propertyset.setComplexProperty(propertyname, complexProperty);
        recorder.modifiedPropertyset(propertyset);
    }

    public Propertyset getReferenceProperty(String propertyname) {
        return propertyset.getReferenceProperty(propertyname);
    }

    public void setReferenceProperty(String propertyname, Propertyset referencedObject) {
        propertyset.setReferenceProperty(propertyname, referencedObject);
        recorder.modifiedPropertyset(propertyset);
    }

    public ValueList getListProperty(String propertyname) {
        return propertyset.getListProperty(propertyname);
    }

    public void setListProperty(String propertyname, ValueList listValue) {
        propertyset.setListProperty(propertyname, listValue);
        recorder.modifiedPropertyset(propertyset);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + propertyset.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() == obj.getClass()) {
            PropertysetRecordingSaveTime other = (PropertysetRecordingSaveTime) obj;
            return propertyset.equals(other.propertyset) && recorder.equals(other.recorder);
        }

        // Will compare equal to an unwrapped Propertyset, but unwrapped propertyset
        // doesn't know of this type and will not compare equal the other way
        return propertyset.equals(obj);
    }

    @Override
    public String toString() {
        return "PropertysetRecordingSaveTime [context=" + recorder + ", propertyset=" + propertyset + "]";
    }

    @Override
    public void clear() {
        propertyset.clear();
    }

    @Override
    public boolean containsKey(Object key) {
        return propertyset.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return propertyset.containsValue(value);
    }

    @Override
    public Set<Entry<String, Value>> entrySet() {
        return propertyset.entrySet();
    }

    @Override
    public Value get(Object key) {
        return propertyset.get(key);
    }

    @Override
    public boolean isEmpty() {
        return propertyset.isEmpty();
    }

    @Override
    public Set<String> keySet() {
        return propertyset.keySet();
    }

    @Override
    public Value put(String key, Value value) {
        return propertyset.put(key, value);
    }

    @Override
    public void putAll(Map<? extends String, ? extends Value> map) {
        propertyset.putAll(map);
    }

    @Override
    public Value remove(Object key) {
        return propertyset.remove(key);
    }

    @Override
    public int size() {
        return propertyset.size();
    }

    @Override
    public Collection<Value> values() {
        return propertyset.values();
    }

}
