package no.priv.bang.modeling.modelstore.value;

import java.util.UUID;

import no.priv.bang.modeling.modelstore.services.Propertyset;
import no.priv.bang.modeling.modelstore.services.Value;
import no.priv.bang.modeling.modelstore.services.ValueList;

import static no.priv.bang.modeling.modelstore.value.Values.*;

class IdValue implements Value {

    private UUID value;

    IdValue(UUID value) {
        if (value != null) {
            this.value = value;
        } else {
            this.value = getNil().asId();
        }
    }

    public boolean isId() {
        return true;
    }

    public UUID asId() {
        return value;
    }

    public boolean isBoolean() {
        return false;
    }

    public Boolean asBoolean() {
        return getNil().asBoolean();
    }

    public boolean isLong() {
        return false;
    }

    public Long asLong() {
        return getNil().asLong();
    }

    public boolean isDouble() {
        return false;
    }

    public Double asDouble() {
        return getNil().asDouble();
    }

    public boolean isString() {
        return false;
    }

    public String asString() {
        return value.toString();
    }

    public boolean isComplexProperty() {
        return false;
    }

    public Propertyset asComplexProperty() {
        return getNil().asComplexProperty();
    }

    public boolean isReference() {
        return false;
    }

    public Propertyset asReference() {
        return getNil().asReference();
    }

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

        IdValue other = (IdValue) obj;
        return value.equals(other.value);
    }

    @Override
    public String toString() {
        return "IdValue [value=" + value + "]";
    }

}
