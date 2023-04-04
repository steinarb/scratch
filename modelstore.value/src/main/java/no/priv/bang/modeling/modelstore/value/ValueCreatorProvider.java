package no.priv.bang.modeling.modelstore.value;

import java.util.UUID;

import no.priv.bang.modeling.modelstore.services.ModificationRecorder;
import no.priv.bang.modeling.modelstore.services.Propertyset;
import no.priv.bang.modeling.modelstore.services.Value;
import no.priv.bang.modeling.modelstore.services.ValueCreator;
import no.priv.bang.modeling.modelstore.services.ValueList;

public class ValueCreatorProvider implements ValueCreator {

    @Override
    public Propertyset getNilPropertyset() {
        return PropertysetNil.getNil();
    }

    @Override
    public Value getNil() {
        return NilValue.getNil();
    }

    @Override
    public Value fromBoolean(Boolean value) {
        return new BooleanValue(value);
    }

    @Override
    public Value fromLong(Long value) {
        return new LongValue(value);
    }

    @Override
    public Value fromDouble(Double value) {
        return new DoubleValue(value);
    }

    @Override
    public Value fromString(String value) {
        return new StringValue(value);
    }

    @Override
    public Value fromValueList(ValueList valueList) {
        return new ListValue(valueList);
    }

    @Override
    public Propertyset newPropertyset() {
        return new PropertysetImpl();
    }

    @Override
    public Propertyset newPropertyset(UUID id) {
        return new PropertysetImpl(id);
    }

    @Override
    public Propertyset wrapInModificationTracker(ModificationRecorder recorder, Propertyset propertyset) {
        return new PropertysetRecordingSaveTime(recorder, propertyset);
    }

    @Override
    public Propertyset unwrapPropertyset(Propertyset propertyset) {
        if (propertyset instanceof PropertysetRecordingSaveTime) {
            PropertysetRecordingSaveTime wrapper = (PropertysetRecordingSaveTime) propertyset;
            return wrapper.getPropertyset();
        }

        return propertyset;
    }

    @Override
    public Value toComplexValue(Propertyset propertyset) {
        return new ComplexValue(propertyset);
    }

    @Override
    public Value toReferenceValue(Propertyset propertyset) {
        return new ReferenceValue(propertyset);
    }

    @Override
    public ValueList newValueList() {
        return new ValueArrayList();
    }

}
