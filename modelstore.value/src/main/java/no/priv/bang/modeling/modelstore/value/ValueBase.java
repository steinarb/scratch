package no.priv.bang.modeling.modelstore.backend;

import java.util.UUID;

import no.priv.bang.modeling.modelstore.services.Value;

import static no.priv.bang.modeling.modelstore.backend.Values.*;


/**
 * Abstract class containing implementations of the type query methods
 * all returning false.
 *
 * This leaves classes actually wrapping a type with only having to
 * override the actual method that will return true.
 *
 */
public abstract class ValueBase implements Value {

    public boolean isId() {
        return false;
    }

    public UUID asId() {
        return getNil().asId();
    }

    public boolean isBoolean() {
        return false;
    }

    public boolean isLong() {
        return false;
    }

    public boolean isDouble() {
        return false;
    }

    public boolean isString() {
        return false;
    }

    public boolean isComplexProperty() {
        return false;
    }

    public boolean isReference() {
        return false;
    }

    public boolean isList() {
        return false;
    }

}
