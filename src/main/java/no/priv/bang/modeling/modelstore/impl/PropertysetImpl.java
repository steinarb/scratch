package no.priv.bang.modeling.modelstore.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import no.priv.bang.modeling.modelstore.Propertyset;
import no.priv.bang.modeling.modelstore.PropertysetNil;
import no.priv.bang.modeling.modelstore.Propertyvalue;
import no.priv.bang.modeling.modelstore.PropertyvalueList;
import no.priv.bang.modeling.modelstore.PropertyvalueNil;

/**
 * Implementation of {@link Propertyset} backed by a {@link Map}.
 *
 * @author Steinar Bang
 *
 */
public class PropertysetImpl implements Propertyset {
    final private String idKey = "id";
    Map<String, Propertyvalue> properties = new HashMap<String, Propertyvalue>();

    public PropertysetImpl(UUID id) {
    	properties.put(idKey, new IdPropertyvalue(id));
    }

    public PropertysetImpl() { }

    public boolean isNil() {
        return false;
    }

    public boolean hasId() {
        return properties.containsKey(idKey);
    }

    public UUID getId() {
    	if (hasId()) {
            return properties.get(idKey).asId();
    	}
		
    	return PropertyvalueNil.getNil().asId();
    }

    public Boolean getBooleanProperty(String propertyName) {
        Propertyvalue rawPropertyValue = properties.get(propertyName);
        if (null != rawPropertyValue) {
            return rawPropertyValue.asBoolean();
        }

        return PropertysetNil.getNil().getBooleanProperty(propertyName);
    }

    public void setBooleanProperty(String propertyName, Boolean boolValue) {
    	if (!idKey.equals(propertyName)) {
            properties.put(propertyName, new BooleanPropertyvalue(boolValue));
    	}
    }

    public Long getLongProperty(String propertyName) {
        Propertyvalue rawPropertyValue = properties.get(propertyName);
        if (null != rawPropertyValue) {
            return rawPropertyValue.asLong();
        }

        return PropertysetNil.getNil().getLongProperty(propertyName);
    }

    public void setLongProperty(String propertyName, Long intValue) {
    	if (!idKey.equals(propertyName)) {
            properties.put(propertyName, new LongPropertyvalue(intValue));
    	}
    }

    public Double getDoubleProperty(String propertyName) {
        Propertyvalue rawPropertyValue = properties.get(propertyName);
        if (null != rawPropertyValue) {
            return rawPropertyValue.asDouble();
        }

        return PropertysetNil.getNil().getDoubleProperty(propertyName);
    }

    public void setDoubleProperty(String propertyName, Double doubleValue) {
    	if (!idKey.equals(propertyName)) {
            properties.put(propertyName, new DoublePropertyvalue(doubleValue));
    	}
    }

    public String getStringProperty(String propertyName) {
        Propertyvalue rawPropertyValue = properties.get(propertyName);
        if (null != rawPropertyValue) {
            return rawPropertyValue.asString();
        }

        return PropertysetNil.getNil().getStringProperty(propertyName);
    }

    public void setStringProperty(String propertyName, String stringValue) {
    	if (!idKey.equals(propertyName)) {
            properties.put(propertyName, new StringPropertyvalue(stringValue));
    	}
    }

    public Propertyset getComplexProperty(String propertyName) {
    	Propertyvalue rawPropertyValue = properties.get(propertyName);
    	if (null != rawPropertyValue) {
            return rawPropertyValue.asComplexProperty();
    	}

        return PropertysetNil.getNil();
    }

    public void setComplexProperty(String propertyName, Propertyset complexProperty) {
    	if (!idKey.equals(propertyName)) {
            properties.put(propertyName, new ComplexPropertyvalue(complexProperty));
    	}
    }

    public Propertyset getReferenceProperty(String propertyName) {
    	Propertyvalue rawPropertyValue = properties.get(propertyName);
    	if (null != rawPropertyValue) {
            return rawPropertyValue.asReference();
    	}

        return PropertysetNil.getNil();
    }

    public void setReferenceProperty(String propertyName, Propertyset referencedObject) {
    	if (!idKey.equals(propertyName)) {
            properties.put(propertyName, new ReferencePropertyvalue(referencedObject));
    	}
    }

    public PropertyvalueList getListProperty(String propertyName) {
    	Propertyvalue rawPropertyValue = properties.get(propertyName);
    	if (null != rawPropertyValue) {
            return rawPropertyValue.asList();
    	}

        return PropertysetNil.getNil().getListProperty(propertyName);
    }

    public void setListProperty(String propertyName, PropertyvalueList listValue) {
    	if (!idKey.equals(propertyName)) {
            properties.put(propertyName, new ListPropertyvalue(listValue));
    	}
    }

}
