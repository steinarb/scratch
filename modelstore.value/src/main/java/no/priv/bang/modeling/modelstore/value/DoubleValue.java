package no.priv.bang.modeling.modelstore.value;

import static no.priv.bang.modeling.modelstore.value.Values.*;

import no.priv.bang.modeling.modelstore.services.Propertyset;
import no.priv.bang.modeling.modelstore.services.ValueList;

/**
 * Wraps a {@link Double} value in a {@link Propertyset}.
 *
 * Will return well defined values if asked to return a different
 * property type.
 *
 */
class DoubleValue extends ValueBase {
    private Double value;

    DoubleValue(Double value) {
        if (null == value) {
            this.value = getNil().asDouble();
        } else {
            this.value = value;
        }
    }

    @Override
    public boolean isDouble() {
        return true;
    }

    public Boolean asBoolean() {
        double floatValue = value.doubleValue();
        return floatValue != 0.0;
    }

    public Long asLong() {
        double floatValue = value.doubleValue();
        return Math.round(floatValue);
    }

    public Double asDouble() {
        return value;
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

        DoubleValue other = (DoubleValue) obj;
        return value.equals(other.value);
    }

    @Override
    public String toString() {
        return "DoubleValue [value=" + value + "]";
    }

}
