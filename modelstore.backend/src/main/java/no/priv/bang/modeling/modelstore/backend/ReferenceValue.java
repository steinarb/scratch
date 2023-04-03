package no.priv.bang.modeling.modelstore.backend;

import static no.priv.bang.modeling.modelstore.backend.Values.*;

import no.priv.bang.modeling.modelstore.services.Propertyset;

/**
 * A property value that references a {@link Propertyset} (a "pointer"
 * value).  This type is essential for building graphs.
 *
 */
class ReferenceValue extends PropertysetValueBase {

    ReferenceValue(Propertyset value) {
        super(value);
    }

    @Override
    public boolean isReference() {
        return true;
    }

    public Propertyset asComplexProperty() {
        return getNilPropertyset();
    }

    public Propertyset asReference() {
        return value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + value.getId().hashCode();
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

        return value.getId().equals(other.value.getId());
    }

    @Override
    public String toString() {
        return "ReferenceValue [value=" + value.getId() + "]";
    }

}
