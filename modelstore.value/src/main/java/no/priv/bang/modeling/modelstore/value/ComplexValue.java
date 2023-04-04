package no.priv.bang.modeling.modelstore.value;

import static no.priv.bang.modeling.modelstore.value.Values.*;

import no.priv.bang.modeling.modelstore.services.Propertyset;


class ComplexValue extends PropertysetValueBase {

    ComplexValue(Propertyset value) {
        super(value);
    }

    @Override
    public boolean isComplexProperty() {
        return true;
    }

    public Propertyset asComplexProperty() {
        return value;
    }

    public Propertyset asReference() {
        return getNilPropertyset();
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

        PropertysetValueBase other = (PropertysetValueBase) obj;
        return value.equals(other.value);
    }

    @Override
    public String toString() {
        return "ComplexValue [value=" + value + "]";
    }

}
