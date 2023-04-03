package no.priv.bang.modeling.modelstore.backend;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import no.priv.bang.modeling.modelstore.services.Propertyset;
import no.priv.bang.modeling.modelstore.services.Value;
import no.priv.bang.modeling.modelstore.services.ValueList;

import static no.priv.bang.modeling.modelstore.backend.Values.*;

class ValueArrayList extends AbstractList<Value> implements ValueList {

    List<Value> arrayList;

    public ValueArrayList(ValueList original) {
        arrayList = new ArrayList<>(original);
        deepDefensiveCopyWhenNeeded();
    }

    public ValueArrayList() {
        this(0);
    }

    public ValueArrayList(int initialCapacity) {
        arrayList = new ArrayList<>(initialCapacity);
    }

    private void deepDefensiveCopyWhenNeeded() {
        for (int i = 0; i < arrayList.size(); i++) {
            Value v = arrayList.get(i);
            if (v.isComplexProperty()) {
                arrayList.set(i, toComplexValue(v.asComplexProperty()));
            } else if (v.isList()) {
                arrayList.set(i, toListValue(v.asList()));
            }
        }
    }

    @Override
    public Value set(int i, Value value) {
        if (null == value) {
            return arrayList.set(i, getNil());
        } else if (value.isComplexProperty()) {
            return arrayList.set(i, toComplexValue(value.asComplexProperty()));
        } else if (value.isList()) {
            return arrayList.set(i, toListValue(value.asList()));
        } else {
            return arrayList.set(i, value);
        }
    }

    public Value set(int i, Boolean value) {
        return arrayList.set(i, toBooleanValue(value));
    }

    public Value set(int i, boolean value) {
        return arrayList.set(i, toBooleanValue(value));
    }

    public Value set(int i, Long value) {
        return arrayList.set(i, toLongValue(value));
    }

    public Value set(int i, long value) {
        return arrayList.set(i, toLongValue(value));
    }

    public Value set(int i, Double value) {
        return arrayList.set(i, toDoubleValue(value));
    }

    public Value set(int i, double value) {
        return arrayList.set(i, toDoubleValue(value));
    }

    public Value set(int i, String value) {
        return arrayList.set(i, toStringValue(value));
    }

    public Value set(int i, Propertyset value) {
        if (value == null || !value.hasId()) {
            return arrayList.set(i, toComplexValue(value));
        } else {
            return arrayList.set(i, toReferenceValue(value));
        }
    }

    public Value set(int i, ValueList value) {
        return arrayList.set(i, toListValue(value));
    }

    @Override
    public void add(int i, Value value) {
        if (value == null) {
            arrayList.add(i, getNil());
        } else if (value.isComplexProperty()) {
            arrayList.add(i, toComplexValue(value.asComplexProperty()));
        } else if (value.isList()) {
            arrayList.add(i, toListValue(value.asList()));
        } else {
            arrayList.add(i, value);
        }
    }

    public void add(Boolean value) {
        arrayList.add(toBooleanValue(value));
    }

    public void add(boolean value) {
        arrayList.add(toBooleanValue(value));
    }

    public void add(Long value) {
        arrayList.add(toLongValue(value));
    }

    public void add(long value) {
        arrayList.add(toLongValue(value));
    }

    public void add(Double value) {
        arrayList.add(toDoubleValue(value));
    }

    public void add(double value) {
        arrayList.add(toDoubleValue(value));
    }

    public void add(String value) {
        arrayList.add(toStringValue(value));
    }

    public void add(Propertyset value) {
        if (value == null || !value.hasId()) {
            arrayList.add(toComplexValue(value));
        } else {
            arrayList.add(toReferenceValue(value));
        }
    }

    public void add(ValueList value) {
        arrayList.add(toListValue(value));
    }

    @Override
    public Value remove(int paramInt) {
        return arrayList.remove(paramInt);
    }

    @Override
    public Value get(int index) {
        return arrayList.get(index);
    }

    @Override
    public int size() {
        return arrayList.size();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + arrayList.hashCode();
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

        ValueArrayList other = (ValueArrayList) obj;
        return arrayList.equals(other.arrayList);
    }

}
