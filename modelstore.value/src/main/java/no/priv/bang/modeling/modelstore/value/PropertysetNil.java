package no.priv.bang.modeling.modelstore.backend;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import no.priv.bang.modeling.modelstore.services.Propertyset;
import no.priv.bang.modeling.modelstore.services.Value;
import no.priv.bang.modeling.modelstore.services.ValueList;


/**
 * Singleton implementation of {@link Propertyset} intended to be used
 * instead of null, for undefined property values, or values that
 * cannot be cast to a {@link Propertyset}
 *
 */
public final class PropertysetNil implements Propertyset {

    private static Propertyset singleton;

    static Propertyset getNil() {
        if (null == singleton) {
            singleton = new PropertysetNil();
        }

        return singleton;
    }

    public void copyValues(Propertyset propertyset) {
        // No-op
    }

    private PropertysetNil() {}

    public boolean isNil() {
        return true;
    }

    public Collection<String> getPropertynames() {
        return Collections.emptyList();
    }

    public Value getProperty(String propertyname) {
        return Values.getNil();
    }

    public void setProperty(String propertyname, Value property) {
        // No-op
    }

    public boolean hasAspect() {
        return false;
    }

    public ValueList getAspects() {
        return Values.getNil().asList();
    }

    public void addAspect(Propertyset aspect) {
        // No-op
    }

    public boolean hasId() {
        return false;
    }

    public UUID getId() {
        return Values.getNil().asId();
    }

    public Boolean getBooleanProperty(String propertyname) {
        return Values.getNil().asBoolean();
    }

    public void setBooleanProperty(String propertyname, Boolean boolValue) {
        // No-op
    }

    public void setBooleanProperty(String propertyname, boolean boolValue) {
        // No-op
    }

    public Long getLongProperty(String propertyname) {
        return Values.getNil().asLong();
    }

    public void setLongProperty(String propertyname, Long intValue) {
        // No-op
    }

    public void setLongProperty(String propertyname, long intvalue) {
        // No-op
    }

    public Double getDoubleProperty(String propertyname) {
        return Values.getNil().asDouble();
    }

    public void setDoubleProperty(String propertyname, Double doubleValue) {
        // No-op
    }

    public void setDoubleProperty(String propertyname, double doubleValue) {
        // No-op
    }

    public String getStringProperty(String propertyname) {
        return Values.getNil().asString();
    }

    public void setStringProperty(String propertyname, String stringValue) {
        // No-op
    }

    public Propertyset getComplexProperty(String propertyname) {
        return Values.getNil().asComplexProperty();
    }

    public void setComplexProperty(String propertyname, Propertyset complexProperty) {
        // No-op
    }

    public Propertyset getReferenceProperty(String propertyname) {
        return Values.getNil().asReference();
    }

    public void setReferenceProperty(String string, Propertyset referencedObject) {
        // No-op
    }

    public ValueList getListProperty(String propertyname) {
        return Values.getNil().asList();
    }

    public void setListProperty(String propertyname, ValueList listValue) {
        // No-op
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (obj instanceof Propertyset) {
            Propertyset other = (Propertyset) obj;
            if (other.getPropertynames().isEmpty()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public String toString() {
        return "PropertysetNil []";
    }

}
