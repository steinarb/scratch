package no.priv.bang.modeling.modelstore.services;

import java.util.Date;

/**
 * Define the interface for recording and retrieving the last
 * modification times for propertysets.
 */
public interface ModificationRecorder {

    /**
     * Set a timestamp for the propertyset given as an argument
     *
     * @param propertyset the {@link Propertyset} to set a timestamp for
     */
    public void modifiedPropertyset(Propertyset propertyset);

    /**
     * Retrieve the last modified date and time for a propertyset.
     *
     * @param propertyset the {@link Propertyset} to find the last modification date and time for
     * @return the last modification date and time for the propertyset in the argument
     */
    public Date getLastmodifieddate(Propertyset propertyset);

}
