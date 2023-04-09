package no.priv.bang.modeling.modelstore.value;

import static no.priv.bang.modeling.modelstore.value.Values.*;

import no.priv.bang.modeling.modelstore.services.Propertyset;
import no.priv.bang.modeling.modelstore.services.ValueList;

/**
 * Wraps a {@link Boolean} value in a {@link Propertyset}.
 *
 * Will return well defined values if asked to return a different
 * property type.
 *
 */
class BooleanValue extends ValueBase {

    private Boolean value;

    BooleanValue(Boolean value) {
        if (null == value) {
            this.value = getNil().asBoolean();
        } else {
            this.value = value;
        }
    }

    @Override
    public boolean isBoolean() {
        return true;
    }

    public Boolean asBoolean() {
        return value;
    }

    public Long asLong() {
        boolean bool = value.booleanValue();
        return Long.valueOf(bool ? 1 : 0);
    }

    public Double asDouble() {
        boolean bool = value.booleanValue();
        return bool ? 1.0 : 0.0;
    }

    public String asString() {
        return value.toString();
    }

    public Propertyset asComplexProperty() {
        return getNilPropertyset();
    }

    public Propertyset asReference() {
        return getNilPropertyset();
    }

    @Override
    public boolean isList() {
        return false;
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

        BooleanValue other = (BooleanValue) obj;
        return value.equals(other.value);
    }

    @Override
    public String toString() {
        return "BooleanValue [value=" + value + "]";
    }

}
