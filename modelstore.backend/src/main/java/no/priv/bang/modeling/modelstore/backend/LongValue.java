package no.priv.bang.modeling.modelstore.backend;

import static no.priv.bang.modeling.modelstore.backend.Values.*;

import no.priv.bang.modeling.modelstore.services.Propertyset;
import no.priv.bang.modeling.modelstore.services.ValueList;

/**
 * Wraps a {@link Long} value in a {@link Propertyset}.
 *
 * Will return well defined values if asked to return a different
 * property type.
 *
 * @author Steinar Bang
 *
 */
class LongValue extends ValueBase {
    private Long value;

    /**
     * Create new instances of {@link LongValue}.
     *
     * @param value the value to wrap
     * @deprecated use {@link Values#toLongValue(Long)} instead
     */
    public LongValue(Long value) {
        if (null == value) {
            this.value = getNil().asLong();
        } else {
            this.value = value;
        }
    }

    @Override
    public boolean isLong() {
        return true;
    }

    public Boolean asBoolean() {
        long intValue = value.longValue();
        return (intValue != 0);
    }

    public Long asLong() {
        return value;
    }

    public Double asDouble() {
        long intValue = value.longValue();
        return (double) intValue;
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

        LongValue other = (LongValue) obj;
        return value.equals(other.value);
    }

    @Override
    public String toString() {
        return "LongValue [value=" + value + "]";
    }

}
