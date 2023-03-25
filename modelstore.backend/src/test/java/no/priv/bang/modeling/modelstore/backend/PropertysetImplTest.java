package no.priv.bang.modeling.modelstore.backend;

import static no.priv.bang.modeling.modelstore.backend.Values.*;
import static org.junit.jupiter.api.Assertions.*;
import static no.priv.bang.modeling.modelstore.backend.Propertysets.*;

import java.util.Collection;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import no.priv.bang.modeling.modelstore.services.ModelContext;
import no.priv.bang.modeling.modelstore.services.Modelstore;
import no.priv.bang.modeling.modelstore.services.Propertyset;
import no.priv.bang.modeling.modelstore.services.Value;
import no.priv.bang.modeling.modelstore.services.ValueList;

class PropertysetImplTest {

    /**
     * Test creating an empty {@link PropertysetImpl} class and verify
     * the expected empty values for all property types.
     */
    @Test
    void testCreateEmptyPropertyset() {
        Propertyset propertyset = new PropertysetImpl();
        assertFalse(propertyset.isNil());

        Boolean boolProperty = propertyset.getBooleanProperty("boolProperty");
        assertEquals(Boolean.valueOf(false), boolProperty);
        Long intProperty = propertyset.getLongProperty("intPropName");
        assertEquals(Long.valueOf(0), intProperty);
        Double doubleProperty = propertyset.getDoubleProperty("doublePropName");
        assertEquals(Double.valueOf(0), doubleProperty);
        String stringProperty = propertyset.getStringProperty("stringProperty");
        assertEquals("", stringProperty);
        Propertyset complexProperty = propertyset.getComplexProperty("complexProperty");
        assertNotNull(complexProperty);
        assertTrue(complexProperty.isNil());
        Propertyset reference = propertyset.getReferenceProperty("reference");
        assertNotNull(reference);
        assertTrue(reference.isNil());
    }

    /**
     * Test getting a bool from various property types
     */
    @Test
    void testGetBooleanProperty() {
        Propertyset propertyset = new PropertysetImpl();

        Boolean trueValue = Boolean.valueOf(true);
        propertyset.setBooleanProperty("trueValue", trueValue);
        Boolean boolValue = propertyset.getBooleanProperty("trueValue");
        assertEquals(trueValue, boolValue);
        propertyset.setBooleanProperty("alsoTrue", true);
        Boolean alsoTrue = propertyset.getBooleanProperty("alsoTrue");
        assertTrue(alsoTrue);

        // Make sure "id" can't be assigned a boolean.
        assertFalse(propertyset.getBooleanProperty("id"));
        propertyset.setBooleanProperty("id", true);
        assertFalse(propertyset.getBooleanProperty("id"));

        // Set int values and read them back as bool values
        // 0 is false, everything else is true
        Long negativeInt = Long.valueOf(-1);
        propertyset.setLongProperty("negativeInt", negativeInt);
        Boolean negativeIntAsBool = propertyset.getBooleanProperty("negativeInt");
        assertEquals(Boolean.valueOf(true), negativeIntAsBool);
        Long nullInt = Long.valueOf(0);
        propertyset.setLongProperty("nullInt", nullInt);
        Boolean nullIntAsBool = propertyset.getBooleanProperty("nullInt");
        assertEquals(Boolean.valueOf(false), nullIntAsBool);
        Long positiveInt = Long.valueOf(1);
        propertyset.setLongProperty("positiveInt", positiveInt);
        Boolean positiveIntAsBool = propertyset.getBooleanProperty("positiveInt");
        assertEquals(Boolean.valueOf(true), positiveIntAsBool);

        // Set a null value and get a false boolean value back
        propertyset.setBooleanProperty("nullValue", null);
        Boolean nullValue = propertyset.getBooleanProperty("nullValue");
        assertEquals(Boolean.valueOf(false), nullValue);

        // Set float values and read them back as bool values
        // 0.0 is false, everything else is true
        Double negativeFloat = Double.valueOf(-1.0);
        propertyset.setDoubleProperty("negativeFloat", negativeFloat);
        Boolean negativeFloatAsBool = propertyset.getBooleanProperty("negativeFloat");
        assertEquals(Boolean.valueOf(true), negativeFloatAsBool);
        Double nullFloat = Double.valueOf(0.0);
        propertyset.setDoubleProperty("nullFloat", nullFloat);
        Boolean nullFloatAsBool = propertyset.getBooleanProperty("nullFloat");
        assertEquals(Boolean.valueOf(false), nullFloatAsBool);
        Double positiveFloat = Double.valueOf(1.0);
        propertyset.setDoubleProperty("positiveFloat", positiveFloat);
        Boolean positiveFloatAsBool = propertyset.getBooleanProperty("positiveFloat");
        assertEquals(Boolean.valueOf(true), positiveFloatAsBool);

        // Set string values and read them back as bool values
        // "true" is true, everything else is false
        String falseString = "false";
        propertyset.setStringProperty("falseString", falseString);
        Boolean falseStringAsBool = propertyset.getBooleanProperty("falseString");
        assertEquals(Boolean.valueOf(false), falseStringAsBool);
        String trueString = "true";
        propertyset.setStringProperty("trueString", trueString);
        Boolean trueStringAsBool = propertyset.getBooleanProperty("trueString");
        assertEquals(Boolean.valueOf(true), trueStringAsBool);
        String notABoolString = "hey there!";
        propertyset.setStringProperty("notABoolString", notABoolString);
        Boolean notABoolStringAsString = propertyset.getBooleanProperty("notABoolString");
        assertEquals(Boolean.valueOf(false), notABoolStringAsString);
    }

    /**
     * Test getting an integer from various property types
     */
    @Test
    void testGetLongProperty() {
        Propertyset propertyset = new PropertysetImpl();
        assertFalse(propertyset.isNil());

        // Verify that a value set as an integer is returned as the same Long value
        Long intValue1 = Long.valueOf(1234);
        propertyset.setLongProperty("intValue", intValue1);
        Long intValue2 = propertyset.getLongProperty("intValue");
        assertEquals(intValue1, intValue2);
        propertyset.setLongProperty("anotherInt", 42);
        Long anotherInt = propertyset.getLongProperty("anotherInt");
        assertEquals(42, anotherInt.longValue());

        // Make sure "id" can't be assigned a long.
        assertEquals(0, propertyset.getLongProperty("id").longValue());
        propertyset.setLongProperty("id", 43);
        assertEquals(0, propertyset.getLongProperty("id").longValue());

        // Set some double values and read them back as Long values
        Double doubleValueContainingInt = Double.valueOf(7);
        propertyset.setDoubleProperty("doubleValueContainingInt", doubleValueContainingInt);
        Long doubleValueReadBackAsInt1 = propertyset.getLongProperty("doubleValueContainingInt");
        assertEquals(Long.valueOf(7), doubleValueReadBackAsInt1);
        Double doubleValueContainingNonInt = Double.valueOf(2.78);
        propertyset.setDoubleProperty("doubleValueReadBackAsInt", doubleValueContainingNonInt);
        Long doubleValueReadBackAsInt2 = propertyset.getLongProperty("doubleValueReadBackAsInt");
        assertEquals(Long.valueOf(3), doubleValueReadBackAsInt2);

        // Set a null value and verify that a zero int value is returned
        propertyset.setLongProperty("nullValue", null);
        Long nullValue = propertyset.getLongProperty("nullValue");
        assertEquals(Long.valueOf(0), nullValue);

        // Set some string values and read them back as Long values
        String stringValueContainingInt = "11";
        propertyset.setStringProperty("stringValueContainingInt", stringValueContainingInt);
        Long stringValueReadBackAsInt1 = propertyset.getLongProperty("stringValueContainingInt");
        assertEquals(Long.valueOf(11), stringValueReadBackAsInt1);
        String stringValueContainingFloat = "2.78";
        propertyset.setStringProperty("stringValueContainingFloat", stringValueContainingFloat);
        Long stringValueReadBackAsInt2 = propertyset.getLongProperty("stringValueContainingFloat");
        assertEquals(Long.valueOf(3), stringValueReadBackAsInt2);
        String stringValueNotParsableAsANumber = "not a number";
        propertyset.setStringProperty("stringValueNotParsableAsANumber", stringValueNotParsableAsANumber);
        Long stringValueReadBackAsInt3 = propertyset.getLongProperty("stringValueNotParsableAsANumber");
        assertEquals(Long.valueOf(0), stringValueReadBackAsInt3);

        // set boolean values and read them back as integers.
        Boolean falseValue = Boolean.valueOf(false);
        propertyset.setBooleanProperty("falseValue", falseValue);
        Long falseAsLong = propertyset.getLongProperty("falseValue");
        assertEquals(Long.valueOf(0), falseAsLong);
        Boolean trueValue = Boolean.valueOf(true);
        propertyset.setBooleanProperty("trueValue", trueValue);
        Long trueAsLong = propertyset.getLongProperty("trueValue");
        assertEquals(Long.valueOf(1), trueAsLong);
    }

    /**
     * Test getting a double from various property types
     */
    @Test
    void testGetDoubleProperty() {
        Propertyset propertyset = new PropertysetImpl();
        assertFalse(propertyset.isNil());

        // Verify that a value set as a double is returned as the same Double value
        Double doubleValue1 = Double.valueOf(3.14);
        propertyset.setDoubleProperty("doubleValue", doubleValue1);
        Double doubleValue2 = propertyset.getDoubleProperty("doubleValue");
        assertEquals(doubleValue1, doubleValue2);
        propertyset.setDoubleProperty("anotherDouble", 2.78);
        Double anotherDouble = propertyset.getDoubleProperty("anotherDouble");
        assertEquals(2.78, anotherDouble, 0.0);

        // Make sure "id" can't be assigned a double.
        assertEquals(0.0, propertyset.getDoubleProperty("id").doubleValue(), 0.0);
        propertyset.setDoubleProperty("id", 43.0);
        assertEquals(0.0, propertyset.getDoubleProperty("id").doubleValue(), 0.0);

        // Verify that setting a null value will return a zero double value.
        propertyset.setDoubleProperty("nullDoubleValue", null);
        Double nullDoubleValue = propertyset.getDoubleProperty("nullDoubleValue");
        assertEquals(Double.valueOf(0.0), nullDoubleValue);

        // Verify that a value set as an int can be returned as a Double containing the same value
        Long intValue1 = Long.valueOf(17);
        propertyset.setLongProperty("intValue1", intValue1);
        Double doubleValue3 = propertyset.getDoubleProperty("intValue1");
        assertEquals(Double.valueOf(17), doubleValue3);

        // Verify that a float value in a string can be returned as a Double containing the same value.
        String stringValueWithFloat = "3.14";
        propertyset.setStringProperty("stringValueWithFloat", stringValueWithFloat);
        Double doubleValue4 = propertyset.getDoubleProperty("stringValueWithFloat");
        assertEquals(Double.valueOf(3.14), doubleValue4);

        // Verify that an int value in a string can be returned as a Double containing the same value
        String stringValueWithInt = "27";
        propertyset.setStringProperty("stringValueWithFloat", stringValueWithInt);
        Double doubleValue5 = propertyset.getDoubleProperty("stringValueWithFloat");
        assertEquals(Double.valueOf(27), doubleValue5);

        // Verify that string not parseable as a numeric value can be returned as a Double with value 0.0
        String stringValueNotParsableAsANumber = "This is not a number";
        propertyset.setStringProperty("stringValueNotParsableAsANumber", stringValueNotParsableAsANumber);
        Double doubleValue6 = propertyset.getDoubleProperty("stringValueNotParsableAsANumber");
        assertEquals(Double.valueOf(0.0), doubleValue6);

        // set boolean values and read them back as floating point numbers.
        Boolean falseValue = Boolean.valueOf(false);
        propertyset.setBooleanProperty("falseValue", falseValue);
        Double falseAsDouble = propertyset.getDoubleProperty("falseValue");
        assertEquals(Double.valueOf(0.0), falseAsDouble);
        Boolean trueValue = Boolean.valueOf(true);
        propertyset.setBooleanProperty("trueValue", trueValue);
        Double trueAsDouble = propertyset.getDoubleProperty("trueValue");
        assertEquals(Double.valueOf(1.0), trueAsDouble);
    }

    /**
     * Test getting a string back from various property types.
     */
    @Test
    void testGetStringProperty() {
        Propertyset propertyset = new PropertysetImpl();

        // Set a string property and get it back as a string
        String stringValue = "Hello world!";
        propertyset.setStringProperty("stringValue", stringValue);
        String stringValueReadBackFromProperty = propertyset.getStringProperty("stringValue");
        assertEquals(stringValue, stringValueReadBackFromProperty);

        // Set a null string property and verify that it is returned as an empty string
        propertyset.setStringProperty("nullValue", null);
        String nullValue = propertyset.getStringProperty("nullValue");
        assertEquals("", nullValue);

        // Set an int property and read it back as a string
        Long intValue = Long.valueOf(42);
        propertyset.setLongProperty("intValue", intValue);
        String intValueAsString = propertyset.getStringProperty("intValue");
        assertEquals("42", intValueAsString);

        // Set a double property and read it back as a string
        Double doubleValue = Double.valueOf(37.5);
        propertyset.setDoubleProperty("doubleValue", doubleValue);
        String doubleValueAsString = propertyset.getStringProperty("doubleValue");
        assertEquals("37.5", doubleValueAsString);

        // set boolean values and read them back as floating point numbers.
        Boolean falseValue = Boolean.valueOf(false);
        propertyset.setBooleanProperty("falseValue", falseValue);
        String falseAsString = propertyset.getStringProperty("falseValue");
        assertEquals("false", falseAsString);
        Boolean trueValue = Boolean.valueOf(true);
        propertyset.setBooleanProperty("trueValue", trueValue);
        String trueAsString = propertyset.getStringProperty("trueValue");
        assertEquals("true", trueAsString);
    }


    /**
     * Test getting a complex property back from various property types.
     *
     * Only a property actually set as a complex property should return
     * something other than {@link PropertysetNil#getNil()},
     */
    @Test
    void testGetComplexProperty() {
        Modelstore modelstore = new ModelstoreProvider();
        ModelContext context = modelstore.getDefaultContext();
        Propertyset propertyset = context.createPropertyset();

        // Set a complex property and retrieve it.
        Propertyset point = context.createPropertyset();
        point.setDoubleProperty("x", 75.3);
        point.setDoubleProperty("y", 145.3);
        propertyset.setComplexProperty("upperLeftCorner", point);
        Propertyset upperLeftCorner = propertyset.getComplexProperty("upperLeftCorner");
        assertEquals(Double.valueOf(75.3), upperLeftCorner.getDoubleProperty("x"));
        assertEquals(Double.valueOf(145.3), upperLeftCorner.getDoubleProperty("y"));

        // Set a null complex property value and verify that it results in a nil Propertyset
        propertyset.setComplexProperty("nullValue", null);
        Propertyset nullValue = propertyset.getComplexProperty("nullValue");
        assertEquals(getNilPropertyset(), nullValue);

        // Check that accessing a complex property as other property types
        // gives null values (a complex property can't be autoconverted to
        // a different type).
        Boolean upperLeftCornerAsBoolean = propertyset.getBooleanProperty("upperLeftCorner");
        assertEquals(Boolean.valueOf(false), upperLeftCornerAsBoolean);
        Long upperLeftCornerAsLong = propertyset.getLongProperty("upperLeftCorner");
        assertEquals(Long.valueOf(0), upperLeftCornerAsLong);
        Double upperLeftCornerAsDouble = propertyset.getDoubleProperty("upperLeftCorner");
        assertEquals(Double.valueOf(0.0), upperLeftCornerAsDouble);
        String upperLeftCornerAsString = propertyset.getStringProperty("upperLeftCorner");
        assertEquals("", upperLeftCornerAsString);
        Propertyset upperLeftCornerAsReference = propertyset.getReferenceProperty("upperLeftCorner");
        assertEquals(getNilPropertyset(), upperLeftCornerAsReference);

        // Set a some properties with different types and try reading them back as complex properties
        // They all result in a nil complex property (ie. no value conversion).
        propertyset.setBooleanProperty("boolValue", Boolean.valueOf(true));
        assertEquals(getNilPropertyset(), propertyset.getComplexProperty("boolValue"));
        propertyset.setLongProperty("longValue", Long.valueOf(42));
        assertEquals(getNilPropertyset(), propertyset.getComplexProperty("longValue"));
        propertyset.setDoubleProperty("doubleValue", Double.valueOf(2.78));
        assertEquals(getNilPropertyset(), propertyset.getComplexProperty("doubleValue"));
        propertyset.setStringProperty("stringValue", "foo bar");
        assertEquals(getNilPropertyset(), propertyset.getComplexProperty("stringValue"));
        Propertyset referencedObject = new PropertysetImpl();
        propertyset.setReferenceProperty("referenceValue", referencedObject);
        assertEquals(getNilPropertyset(), propertyset.getComplexProperty("referenceValue"));

        // Verify deep copy on set
        Propertyset otherpropertyset = context.createPropertyset();
        otherpropertyset.setComplexProperty("corner", propertyset.getComplexProperty("upperLeftCorner"));
        otherpropertyset.getComplexProperty("corner").setDoubleProperty("x", 77);
        assertEquals(77.0, otherpropertyset.getComplexProperty("corner").getDoubleProperty("x"), 0.0);
        assertEquals(75.3, propertyset.getComplexProperty("upperLeftCorner").getDoubleProperty("x"), 0.0, "Expected original to be unchanged");
    }

    /**
     * Test getting a complex property back from various property types.
     *
     * Only a property actually set as a complex property should return
     * something other than {@link PropertysetNil#getNil()},
     */
    @Test
    void testGetReferenceProperty() {
        Propertyset propertyset = new PropertysetImpl();

        // Set an object reference property value and read it back
        Propertyset aDifferentPropset = new PropertysetImpl();
        aDifferentPropset.setStringProperty("foo", "bar");
        propertyset.setReferenceProperty("aLinkedObject", aDifferentPropset);
        Propertyset aLinkedObject = propertyset.getReferenceProperty("aLinkedObject");
        assertEquals(aDifferentPropset, aLinkedObject);

        // Check that accessing a reference property as other property types
        // gives null values (a reference property can't be autoconverted to
        // a different type).
        Boolean aLinkedObjectAsBoolean = propertyset.getBooleanProperty("aLinkedObject");
        assertEquals(Boolean.valueOf(false), aLinkedObjectAsBoolean);
        Long aLinkedObjectAsLong = propertyset.getLongProperty("aLinkedObject");
        assertEquals(Long.valueOf(0), aLinkedObjectAsLong);
        Double aLinkedObjectAsDouble = propertyset.getDoubleProperty("aLinkedObject");
        assertEquals(Double.valueOf(0.0), aLinkedObjectAsDouble);
        String aLinkedObjectAsString = propertyset.getStringProperty("aLinkedObject");
        assertEquals("", aLinkedObjectAsString);
        Propertyset aLinkedObjectAsComplexProperty = propertyset.getComplexProperty("aLinkedObject");
        assertEquals(getNilPropertyset(), aLinkedObjectAsComplexProperty);

        // Set a some properties with different types and try reading them back as reference properties
        // They all result in a nil reference property (ie. no value conversion).
        propertyset.setBooleanProperty("boolValue", Boolean.valueOf(true));
        assertEquals(getNilPropertyset(), propertyset.getReferenceProperty("boolValue"));
        propertyset.setLongProperty("longValue", Long.valueOf(42));
        assertEquals(getNilPropertyset(), propertyset.getReferenceProperty("longValue"));
        propertyset.setDoubleProperty("doubleValue", Double.valueOf(2.78));
        assertEquals(getNilPropertyset(), propertyset.getReferenceProperty("doubleValue"));
        propertyset.setStringProperty("stringValue", "foo bar");
        assertEquals(getNilPropertyset(), propertyset.getReferenceProperty("stringValue"));
        Propertyset complexdObject = new PropertysetImpl();
        propertyset.setComplexProperty("complexValue", complexdObject);
        assertEquals(getNilPropertyset(), propertyset.getReferenceProperty("complexValue"));
    }

    /**
     * Test getting a list property back from various types.
     */
    @Test
    void testGetListProperty() {
        Modelstore modelstore = new ModelstoreProvider();
        ModelContext context = modelstore.getDefaultContext();
        Propertyset propertyset = context.createPropertyset();

        // Set and get a list value, and verify that its members can be accessed.
        ValueList listValue = newList();
        listValue.add(toBooleanValue(Boolean.valueOf(true)));
        listValue.add(toLongValue(Long.valueOf(42)));
        propertyset.setListProperty("listValue", listValue);
        ValueList retrievedListValue = propertyset.getListProperty("listValue");
        assertEquals(2, retrievedListValue.size());
        assertEquals(Long.valueOf(42), retrievedListValue.get(1).asLong());

        // Expect and empty and unmodifiable list when accessing a property set with a null value
        propertyset.setListProperty("nullValue", null);
        ValueList nullValue = propertyset.getListProperty("nullValue");
        assertEquals(0, nullValue.size());

        // Expect an empty and unmodifiable list when accessing a non-existing property.
        ValueList emptyList = propertyset.getListProperty("noSuchList");
        assertEquals(0, emptyList.size());

        // Expect all non-list properties to result in an empty list when accessing them
        propertyset.setBooleanProperty("boolValue", Boolean.valueOf(true));
        assertEquals(getNil().asList(), propertyset.getListProperty("boolValue"));
        propertyset.setLongProperty("longValue", Long.valueOf(42));
        assertEquals(getNil().asList(), propertyset.getListProperty("longValue"));
        propertyset.setDoubleProperty("doubleValue", Double.valueOf(2.78));
        assertEquals(getNil().asList(), propertyset.getListProperty("doubleValue"));
        propertyset.setStringProperty("stringValue", "foo bar");
        assertEquals(getNil().asList(), propertyset.getListProperty("stringValue"));
        Propertyset complexdObject = new PropertysetImpl();
        propertyset.setComplexProperty("complexValue", complexdObject);
        assertEquals(getNil().asList(), propertyset.getListProperty("complexValue"));
        Propertyset referencedObject = new PropertysetImpl();
        propertyset.setReferenceProperty("referencedObject", referencedObject);
        assertEquals(getNil().asList(), propertyset.getListProperty("referencedObject"));

        // Verify deep copy on set
        Propertyset otherpropertyset = context.createPropertyset();
        otherpropertyset.setListProperty("listValue", propertyset.getListProperty("listValue"));
        otherpropertyset.getListProperty("listValue").add(378);
        assertEquals(3, otherpropertyset.getListProperty("listValue").size());
        assertEquals(2, propertyset.getListProperty("listValue").size(), "Expected original to be unchanged");
    }

    @Test
    void testEmptyPropertysetEqualsPropertysetNil() {
        Propertyset emptypropertyset = new PropertysetImpl();
        Propertyset nil = getNilPropertyset();
        assertTrue(emptypropertyset.equals(nil));
        assertTrue(nil.equals(emptypropertyset));
    }

    @Test
    void testGetProperty() {
        Modelstore modelstore = new ModelstoreProvider();
        ModelContext context = modelstore.getDefaultContext();
        Propertyset emptypropertyset = context.createPropertyset();
        Value nosuchproperty = emptypropertyset.getProperty("nosuchproperty");
        assertEquals(getNil(), nosuchproperty);
        Propertyset propertyset = context.createPropertyset();
        propertyset.setProperty("null", null);
        assertEquals(getNilPropertyset(), propertyset.getComplexProperty("null"));

        // Verify deep copy for complex and list properties
        propertyset.setComplexProperty("a", context.createPropertyset());
        propertyset.getComplexProperty("a").setLongProperty("l", 42);
        propertyset.setListProperty("b", newList());
        propertyset.getListProperty("b").add(345);

        Propertyset otherpropertyset = context.createPropertyset();
        otherpropertyset.setProperty("a", propertyset.getProperty("a"));
        otherpropertyset.getComplexProperty("a").setLongProperty("l", 43);
        assertEquals(43, otherpropertyset.getComplexProperty("a").getLongProperty("l").longValue());
        assertEquals(42, propertyset.getComplexProperty("a").getLongProperty("l").longValue(), "Expected original value to be unchanged");
        otherpropertyset.setProperty("b", propertyset.getProperty("b"));
        otherpropertyset.getListProperty("b").add("wow");
        assertEquals(2, otherpropertyset.getListProperty("b").size());
        assertEquals(1, propertyset.getListProperty("b").size(), "Expected original value to be unchanged");
    }

    /**
     * Corner case unit tests for {@link Propertyset#setProperty(String, Value)}.
     */
    @Test
    void testSetProperty() {
        Modelstore modelstore = new ModelstoreProvider();
        ModelContext context = modelstore.createContext();

        // Verify that id can't be set
        Value idValue = createIdValue(context);
        Propertyset propertyset = context.createPropertyset();
        propertyset.setProperty(idKey, idValue);
        assertEquals(getNil(), propertyset.getProperty(idKey));
        assertEquals(getNilPropertyset().getId(), propertyset.getId());

        // Verify that aspects can't be set as a property
        Value aspectsValue = createAspectsValue(context);
        propertyset.setProperty(aspectsKey, aspectsValue);
        assertEquals(getNil(), propertyset.getProperty(aspectsKey));
        assertFalse(propertyset.hasAspect());
        assertEquals(0, propertyset.getAspects().size());

        // Verify that a regular value can be set and retrieved
        // (reusing the aspectsValue which is a list of references.)
        propertyset.setProperty("reference_list", aspectsValue);
        assertEquals(aspectsValue, propertyset.getProperty("reference_list"));
    }

    @Test
    void testHashCode() {
        Propertyset emptypropertyset = new PropertysetImpl();
        assertEquals(31, emptypropertyset.hashCode());
        ModelContext context = new ModelstoreProvider().getDefaultContext();
        UUID id = UUID.fromString("8ce20479-8876-4d84-98a3-c14b53715c5d");
        Propertyset propertyset = new PropertysetImpl();
        populatePropertyset(propertyset, context, id);
        assertEquals(201512373, propertyset.hashCode());
    }

    @Test
    void testEquals() {
        Propertyset emptypropertyset = new PropertysetImpl();
        assertTrue(emptypropertyset.equals(emptypropertyset));
        assertFalse(emptypropertyset.equals(null));
        assertTrue(emptypropertyset.equals(getNilPropertyset()));

        // Compare two identical but propertysets that aren't the same object
        ModelContext context = new ModelstoreProvider().getDefaultContext();
        UUID id = UUID.randomUUID();
        Propertyset propertyset1 = new PropertysetImpl();
        populatePropertyset(propertyset1, context, id);
        Propertyset propertyset2 = new PropertysetImpl();
        populatePropertyset(propertyset2, context, id);
        assertTrue(propertyset1.equals(propertyset2));
        assertTrue(propertyset2.equals(propertyset1));
    }

    @Test
    void testToString() {
        Propertyset emptypropertyset = new PropertysetImpl();
        assertEquals("PropertysetImpl [properties={}]", emptypropertyset.toString());
        // No test for a propertyset with properties, because they are written in a non-predictable order
    }

    @Test
    void testCopyConstructor() {
        ModelContext context = new ModelstoreProvider().getDefaultContext();
        UUID id = UUID.randomUUID();
        Propertyset propertyset = new PropertysetImpl();
        populatePropertyset(propertyset, context, id);

        Propertyset copy = new PropertysetImpl(propertyset);
        compareOriginalUnchangedByCopyChange(context, propertyset, copy);
    }

    @Test
    void testCopyConstructorOnPropertysetWithIdAndAspect() {
        ModelContext context = new ModelstoreProvider().getDefaultContext();
        UUID id = UUID.randomUUID();
        UUID propsetId = UUID.randomUUID();
        Propertyset propertyset = context.findPropertyset(propsetId);
        UUID aspectId = UUID.randomUUID();
        Propertyset aspect = context.findPropertyset(aspectId);
        propertyset.addAspect(aspect);
        populatePropertyset(propertyset, context, id);

        Propertyset copy = new PropertysetImpl(propertyset);
        compareOriginalUnchangedByCopyChange(context, propertyset, copy);
    }

    @Test
    void testCopyValues() {
        ModelContext context = new ModelContextImpl();
        Propertyset propertyset = context.createPropertyset();
        UUID referencedPropertysetId = UUID.randomUUID();
        populatePropertyset(propertyset, context, referencedPropertysetId);

        // Make a copy to compare with afterwards
        PropertysetImpl copyOfPropertyset = new PropertysetImpl(propertyset);
        assertNotSame(propertyset, copyOfPropertyset);
        assertEquals(propertyset, copyOfPropertyset);

        // Try copying in a null propertyset
        propertyset.copyValues(null);

        // Expected the propertyset to be unchanged by the null-copying
        assertEquals(propertyset, copyOfPropertyset);

        // Copy from a propertyset with id
        Propertyset propertysetWithId = context.findPropertyset(UUID.randomUUID());
        populatePropertyset(propertysetWithId, context, referencedPropertysetId);

        // propertysets are not equal because one has an id
        assertNotEquals(propertyset, propertysetWithId);

        // Make a non-id copy of the propertyset with id
        Propertyset copyOfPropertysetWithId = context.createPropertyset();
        copyOfPropertysetWithId.copyValues(propertysetWithId);

        // The copy if the propertyset with id is equal to the propertyset without id
        assertEquals(propertyset, copyOfPropertysetWithId);
    }

    @Test
    void testCopyValuesOnPropertysetWithIdAndAspect() {
        ModelContext context = new ModelContextImpl();
        UUID id = UUID.randomUUID();
        UUID propsetId = UUID.randomUUID();
        Propertyset propertyset = context.findPropertyset(propsetId);
        UUID aspectId = UUID.randomUUID();
        Propertyset aspect = context.findPropertyset(aspectId);
        propertyset.addAspect(aspect);
        populatePropertyset(propertyset, context, id);

        // Create a Propertyset with the same id in a different context and assign the values of the first
        ModelContext context2 = new ModelContextImpl();
        Propertyset copy = context2.findPropertyset(propsetId);
        copy.copyValues(propertyset);
        assertNotSame(propertyset, copy); // Obviously...
        // Compare the inner Propertyset since the metadata-recording wrappers compare context in their equals methods
        assertEquals(propertyset, findWrappedPropertyset(copy));

        // Create a Propertyset without id and copy into it and compare
        Propertyset propertysetWithoutId = context.createPropertyset();
        propertysetWithoutId.copyValues(propertyset);
        assertNotSame(propertyset, propertysetWithoutId); // Obviously...
        assertNotEquals(propertyset, propertysetWithoutId); // No "id" on one of them, so not the same
        assertEquals(propertyset.getAspects(), propertysetWithoutId.getAspects());
        assertAllPropertiesExceptIdAndAspectEquals(propertyset, propertysetWithoutId);

        // Test copy to a propertyset with a different id in the same context
        Propertyset propertysetWithNewId = context.findPropertyset(UUID.randomUUID());
        propertysetWithNewId.copyValues(propertyset);
        assertNotEquals(propertyset, propertysetWithNewId); // Different "id" on the two
        assertEquals(propertyset.getAspects(), propertysetWithNewId.getAspects());
        assertAllPropertiesExceptIdAndAspectEquals(propertyset, propertysetWithNewId);
    }

    private void assertAllPropertiesExceptIdAndAspectEquals(Propertyset propertyset1, Propertyset propertyset2) {
        Collection<String> propertynames1 = propertyset1.getPropertynames();
        propertynames1.remove(idKey);
        propertynames1.remove(aspectsKey);
        Collection<String> propertynames2 = propertyset2.getPropertynames();
        propertynames2.remove(idKey);
        propertynames2.remove(aspectsKey);
        assertEquals(propertynames1, propertynames2);
        for (String propertyname : propertynames1) {
            Value value1 = propertyset1.getProperty(propertyname);
            Value value2 = propertyset2.getProperty(propertyname);
            assertEquals(value1, value2);
        }
    }

    private void compareOriginalUnchangedByCopyChange(ModelContext context, Propertyset propertyset, Propertyset copy) {
        assertNotSame(propertyset, copy); // Obviously...
        assertEquals(propertyset, copy);

        // Check that modifying values in the copy doesn't affect the original
        copy.setBooleanProperty("a", false);
        assertFalse(copy.getBooleanProperty("a").booleanValue());
        assertTrue(propertyset.getBooleanProperty("a").booleanValue(), "Expected unchanged value");
        copy.setLongProperty("b", 43);
        assertEquals(43, copy.getLongProperty("b").longValue());
        assertEquals(42, propertyset.getLongProperty("b").longValue(), "Expected unchanged value");
        copy.setDoubleProperty("c", 1.1);
        assertEquals(1.1, copy.getDoubleProperty("c"), 0.0);
        assertEquals(2.7, propertyset.getDoubleProperty("c"), 0.0, "Expected unchanged value");
        copy.setStringProperty("d", "bar foo");
        assertEquals("bar foo", copy.getStringProperty("d"));
        assertEquals("foo bar", propertyset.getStringProperty("d"), "Expected unchanged value");
        Propertyset originalReferencedPropertyset = copy.getReferenceProperty("e");
        Propertyset newReferencedPropertyset = context.findPropertyset(UUID.randomUUID());
        copy.setReferenceProperty("e", newReferencedPropertyset);
        assertEquals(newReferencedPropertyset, copy.getReferenceProperty("e"));
        assertEquals(originalReferencedPropertyset, propertyset.getReferenceProperty("e"), "Expected unchanged value");
        copy.getComplexProperty("f").setLongProperty("z", 35);
        assertEquals(1, copy.getComplexProperty("f").getPropertynames().size());
        assertEquals(0, propertyset.getComplexProperty("f").getPropertynames().size(), "Expected unchanged value");
        copy.getListProperty("g").add(3.7);
        assertEquals(2, copy.getListProperty("g").size());
        assertEquals(1, propertyset.getListProperty("g").size(), "Expected unchanged value");
    }

    private Value createIdValue(ModelContext context) {
        Propertyset propertysetWithId = context.findPropertyset(UUID.randomUUID());
        Value idValue = propertysetWithId.getProperty(idKey);
        return idValue;
    }

    private Value createAspectsValue(ModelContext context) {
        ValueList aspectlist = context.createList();
        aspectlist.add(context.findPropertyset(UUID.randomUUID()));
        aspectlist.add(context.findPropertyset(UUID.randomUUID()));
        aspectlist.add(context.findPropertyset(UUID.randomUUID()));
        Value aspectsValue = Values.toListValue(aspectlist, false);
        return aspectsValue;
    }

    private void populatePropertyset(Propertyset propertyset, ModelContext context, UUID id) {
        propertyset.setBooleanProperty("a", true);
        propertyset.setLongProperty("b", 42);
        propertyset.setDoubleProperty("c", 2.7);
        propertyset.setStringProperty("d", "foo bar");
        propertyset.setReferenceProperty("e", context.findPropertyset(id));
        propertyset.setComplexProperty("f", context.createPropertyset());
        ValueList list = newList();
        list.add("foobar");
        propertyset.setListProperty("g", list);
    }

}
