package no.priv.bang.modeling.modelstore.value;

import static no.priv.bang.modeling.modelstore.value.Values.*;

import no.priv.bang.modeling.modelstore.services.Propertyset;
import no.priv.bang.modeling.modelstore.services.ValueList;

class ListValue extends ValueBase {

    private ValueList value;

    ListValue(ValueList value) {
        if (null == value) {
            this.value = getNil().asList();
        } else {
            this.value = value;
        }
    }

    public Boolean asBoolean() {
        return getNil().asBoolean();
    }

    public Long asLong() {
        return getNil().asLong();
    }

    public Double asDouble() {
        return getNil().asDouble();
    }

    public String asString() {
        return getNil().asString();
    }

    public Propertyset asComplexProperty() {
        return getNil().asComplexProperty();
    }

    public Propertyset asReference() {
        return getNil().asReference();
    }

    @Override
    public boolean isList() {
        return true;
    }

    public ValueList asList() {
        return value;
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

        if (obj instanceof NilValue) {
            // If this list is empty test equal to a nil property value.
            // If not empty, they are not equal
            return value.isEmpty();
        }

        if (obj == null) {
            return false;
        }


        if (getClass() != obj.getClass()) {
            return false;
        }

        ListValue other = (ListValue) obj;
        return value.equals(other.value);
    }

    @Override
    public String toString() {
        return "ListValue [value=" + value + "]";
    }

}
