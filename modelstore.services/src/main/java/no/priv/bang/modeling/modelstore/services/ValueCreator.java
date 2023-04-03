package no.priv.bang.modeling.modelstore.services;

import java.util.UUID;

/**
 * An interface defining an OSGi service containing methods to convert
 * {@link Boolean}, {@link Long}, {@link Double} and {@link String}
 * to {@link Value} objects.
 *
 * There are also methods to create a new {@link Propertyset} instance
 * and to convert a {@link Propertyset} to a complex value (nested object)
 * and to convert a {@link Propertyset} to a reference value (a "pointer").
 */
public interface ValueCreator {

    /**
     * Return an object that can be used to represent undefined {@link Propertyset} values
     *
     * @return an unmodifiable {@link Value} object that tests as a {@link Propertyset} value
     */
    Propertyset getNilPropertyset();

    /**
     * Return an object that can be used to represent undefined {@link Value} values
     *
     * @return and unmodifiable {@link Value} that represents undefined values
     */
    Value getNil();

    /**
     * Convert a {@link Boolean} into {@link Value}.
     *
     * @param value a {@link Boolean} to convert
     * @return a {@link Value} that will test out as a boolean value
     */
    Value fromBoolean(Boolean value);

    /**
     * Convert a {@link Long} into {@link Value}.
     *
     * @param value a {@link Long} to convert
     * @return a {@link Value} that will test out as a long integer value
     */
    Value fromLong(Long value);

    /**
     * Convert a {@link Double} into {@link Value}.
     *
     * @param value a {@link Double} to convert
     * @return a {@link Value} that will test out as a floating point number value
     */
    Value fromDouble(Double value);

    /**
     * Convert a {@link String} into {@link Value}.
     *
     * @param value a {@link Double} to convert
     * @return a {@link Value} that will test out as a string value
     */
    Value fromString(String value);

    /**
     * Create a new, empty, id-less {@link Propertyset}.
     * Id-less propertysets are intended as complex properties.
     *
     * @param id the unique identifier of the new {@link Propertyset}
     * @return a new {@link Propertyset} value
     */
    Propertyset newPropertyset();

    /**
     * Create a new, empty {@link Propertyset} with a given id value
     *
     * @param id the unique identifier of the new {@link Propertyset}
     * @return a new {@link Propertyset} value
     */
    Propertyset newPropertyset(UUID id);

    /**
     * Create an embedded object value from a propertyset
     *
     * @param propertyset the object to put in the value
     * @return a complex object {@link Value}
     */
    Value toComplexValue(Propertyset propertyset);

    /**
     * Create a reference value to a propertyset (a "pointer to an object")
     *
     * @param propertyset the object to create a pointer to
     * @return a "pointer" {@link Value}
     */
    Value toReferenceValue(Propertyset propertyset);

}
