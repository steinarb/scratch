package no.priv.bang.modeling.modelstore.backend;

import static no.priv.bang.modeling.modelstore.backend.Values.*;

import no.priv.bang.modeling.modelstore.services.Propertyset;
import no.priv.bang.modeling.modelstore.services.ValueList;

/**
 * Wraps a {@link String} value in a {@link Propertyset}.
 *
 * Will return well defined values if asked to return a different
 * property type.
 *
 */
class StringValue extends ValueBase {
    private String value;

    StringValue(String value) {
        if (null == value) {
            this.value = getNil().asString();
        } else {
            this.value = value;
        }
    }

    @Override
    public boolean isString() {
        return true;
    }

    public Boolean asBoolean() {
        return Boolean.valueOf(value);
    }

    public Long asLong() {
        try {
            return Long.valueOf(value);
        } catch (NumberFormatException e1) {
            try {
                double doubleValue = Double.parseDouble(value);
                return Math.round(doubleValue);
            } catch (NumberFormatException e2) {
                return getNilPropertyset().getLongProperty("dummy");
            }
        }
    }

    public Double asDouble() {
        try {
            return Double.valueOf(value);
        } catch (NumberFormatException e) {
            return getNilPropertyset().getDoubleProperty("dummy");
        }
    }

    public String asString() {
        return value;
    }

    public Propertyset asComplexProperty() {
        return getNilPropertyset();
    }

    public Propertyset asReference() {
        return getNilPropertyset();
    }

    public ValueList asList() {
        return getNil().asList();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + value.hashCode();
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

        if (getClass() != obj.getClass()) {
            return false;
        }

        StringValue other = (StringValue) obj;
        return value.equals(other.value);
    }

    @Override
    public String toString() {
        return "StringValue [value=" + value + "]";
    }

}
