package no.priv.bang.modeling.modelstore.backend;

import java.util.Collection;
import java.util.UUID;

import no.priv.bang.modeling.modelstore.services.Propertyset;
import no.priv.bang.modeling.modelstore.services.Value;
import no.priv.bang.modeling.modelstore.services.ValueList;

/**
 * This is an implementation of {@link Propertyset} that wraps a
 * {@link PropertysetImpl} object, and has a back reference to
 * the {@link ModelContextRecordingMetadata} that is used
 * to set the lastmodifiedtime of the {@link Propertyset}.
 *
 */
class PropertysetRecordingSaveTime implements Propertyset {

    private ModelContextRecordingMetadata context;
    private Propertyset propertyset;

    public PropertysetRecordingSaveTime(ModelContextRecordingMetadata context, Propertyset propertyset) {
        this.context = context;
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
        context.modifiedPropertyset(propertyset);
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
        context.modifiedPropertyset(propertyset);
    }

    public void setBooleanProperty(String propertyname, boolean boolValue) {
        propertyset.setBooleanProperty(propertyname, boolValue);
        context.modifiedPropertyset(propertyset);
    }

    public Long getLongProperty(String propertyname) {
        return propertyset.getLongProperty(propertyname);
    }

    public void setLongProperty(String propertyname, Long intValue) {
        propertyset.setLongProperty(propertyname, intValue);
        context.modifiedPropertyset(propertyset);
    }

    public void setLongProperty(String propertyname, long intvalue) {
        propertyset.setLongProperty(propertyname, intvalue);
        context.modifiedPropertyset(propertyset);
    }

    public Double getDoubleProperty(String propertyname) {
        return propertyset.getDoubleProperty(propertyname);
    }

    public void setDoubleProperty(String propertyname, Double doubleValue) {
        propertyset.setDoubleProperty(propertyname, doubleValue);
        context.modifiedPropertyset(propertyset);
    }

    public void setDoubleProperty(String propertyname, double doubleValue) {
        propertyset.setDoubleProperty(propertyname, doubleValue);
        context.modifiedPropertyset(propertyset);
    }

    public String getStringProperty(String propertyname) {
        return propertyset.getStringProperty(propertyname);
    }

    public void setStringProperty(String propertyname, String stringValue) {
        propertyset.setStringProperty(propertyname, stringValue);
        context.modifiedPropertyset(propertyset);
    }

    public Propertyset getComplexProperty(String propertyname) {
        return propertyset.getComplexProperty(propertyname);
    }

    public void setComplexProperty(String propertyname, Propertyset complexProperty) {
        propertyset.setComplexProperty(propertyname, complexProperty);
        context.modifiedPropertyset(propertyset);
    }

    public Propertyset getReferenceProperty(String propertyname) {
        return propertyset.getReferenceProperty(propertyname);
    }

    public void setReferenceProperty(String propertyname, Propertyset referencedObject) {
        propertyset.setReferenceProperty(propertyname, referencedObject);
        context.modifiedPropertyset(propertyset);
    }

    public ValueList getListProperty(String propertyname) {
        return propertyset.getListProperty(propertyname);
    }

    public void setListProperty(String propertyname, ValueList listValue) {
        propertyset.setListProperty(propertyname, listValue);
        context.modifiedPropertyset(propertyset);
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
            return propertyset.equals(other.propertyset) && context.equals(other.context);
        }

        // Will compare equal to an unwrapped Propertyset, but unwrapped propertyset
        // doesn't know of this type and will not compare equal the other way
        return propertyset.equals(obj);
    }

    @Override
    public String toString() {
        return "PropertysetRecordingSaveTime [context=" + context + ", propertyset=" + propertyset + "]";
    }

}
