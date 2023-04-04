package no.priv.bang.modeling.modelstore.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class ValueCreatorTest {

    @Test
    void testService() {
        var service = mock(ValueCreator.class);
        Propertyset nilPropertySet = service.getNilPropertyset();
        assertNull(nilPropertySet);
        Value nil = service.getNil();
        assertNull(nil);
        Value booleanValue1 = service.fromBoolean(Boolean.valueOf(true));
        assertNull(booleanValue1);
        Value booleanValue2 = service.fromBoolean(true);
        assertNull(booleanValue2);
        Value longValue1 = service.fromLong(Long.valueOf(42L));
        assertNull(longValue1);
        Value longValue2 = service.fromLong(42L);
        assertNull(longValue2);
        Value doubleValue1 = service.fromDouble(Double.valueOf(3.14));
        assertNull(doubleValue1);
        Value doubleValue2 = service.fromDouble(3.14);
        assertNull(doubleValue2);
        Value stringValue = service.fromString("foo");
        assertNull(stringValue);
        Propertyset propertyset = service.newPropertyset(UUID.randomUUID());
        assertNull(propertyset);
        Value complexValue = service.toComplexValue(propertyset);
        assertNull(complexValue);
        Value referenceValue = service.toReferenceValue(propertyset);
        assertNull(referenceValue);
        ValueList valueList = service.newValueList();
        assertNull(valueList);
        Value valueListValue = service.fromValueList(valueList);
        assertNull(valueListValue);
    }

}
