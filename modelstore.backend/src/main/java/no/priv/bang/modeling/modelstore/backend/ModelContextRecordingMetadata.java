package no.priv.bang.modeling.modelstore.backend;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import no.priv.bang.modeling.modelstore.services.ModelContext;
import no.priv.bang.modeling.modelstore.services.Propertyset;
import no.priv.bang.modeling.modelstore.services.ValueList;

import static no.priv.bang.modeling.modelstore.backend.Aspects.*;
import static no.priv.bang.modeling.modelstore.backend.ModelContexts.*;

public class ModelContextRecordingMetadata implements ModelContext {

    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
    private ModelContext impl;
    private Map<UUID,Date> lastmodifiedtime = new HashMap<UUID, Date>();

    public ModelContextRecordingMetadata(ModelContext nonMetadataRecordingContext) {
        impl = nonMetadataRecordingContext;
        Propertyset metadata = impl.findPropertyset(metadataId);
        setLastmodifiedtimes(metadata);
    }

    /**
     * UUIDs of propertysets are the propertynames of the "lastmodifiedtimes"
     * complex property.  The values are strings containing {@link Date} values
     * from the propertyset's last modified date.
     *
     * @param metadata a {@link Propertyset} to extract saved last modified times from
     */
    void setLastmodifiedtimes(Propertyset metadata) {
        Propertyset lastmodifiedtimes = metadata.getComplexProperty("lastmodifiedtimes");
        for (String uuidAsString : lastmodifiedtimes.getPropertynames()) {
            String dateAsString = lastmodifiedtimes.getStringProperty(uuidAsString);
            try {
                lastmodifiedtime.put(UUID.fromString(uuidAsString), dateFormat.parse(dateAsString));
            } catch (IllegalArgumentException e) {
                logError("Metadata \"lastmodifiedtime\" Propertyset id not parsable as a UUID", uuidAsString, e);
            } catch (ParseException e) {
                logError("Metadata \"lastmodifiedtime\" value not parsable as a Date", dateAsString, e);
            }
        }
    }

    ModelContext getWrappedModelContext() {
        return impl;
    }

    public ValueList createList() {
        return impl.createList();
    }

    public Propertyset createPropertyset() {
        return impl.createPropertyset();
    }

    public Propertyset findPropertyset(UUID id) {
        return new PropertysetRecordingSaveTime(this, impl.findPropertyset(id));
    }

    public Collection<Propertyset> listAllPropertysets() {
        Collection<Propertyset> implist = impl.listAllPropertysets();
        ArrayList<Propertyset> retlist = new ArrayList<Propertyset>(implist.size() + 1);
        retlist.add(createMetadata());
        for (Propertyset propertyset : implist) {
            if (!metadataId.equals(propertyset.getId())) {
                retlist.add(propertyset);
            }
        }

        return retlist;
    }

    public Collection<Propertyset> listAllAspects() {
        return impl.listAllAspects();
    }

    public Collection<Propertyset> findObjectsOfAspect(Propertyset aspect) {
        if (aspect instanceof PropertysetRecordingSaveTime) {
            PropertysetRecordingSaveTime outerAspect = (PropertysetRecordingSaveTime) aspect;
            return impl.findObjectsOfAspect(outerAspect.getPropertyset());
        }

        return impl.findObjectsOfAspect(aspect);
    }

    /**
     * Set a timestamp for the propertyset given as an argument
     *
     * @param propertyset the {@link Propertyset} to set a timestamp for
     */
    public void modifiedPropertyset(Propertyset propertyset) {
        lastmodifiedtime.put(propertyset.getId(), new Date());
    }

    public Date getLastmodifieddate(Propertyset propertyset) {
        return lastmodifiedtime.get(propertyset.getId());
    }

    private Propertyset createMetadata() {
        Propertyset lastmodifiedtimes = impl.createPropertyset();
        for (Entry<UUID, Date> modifiedtime : lastmodifiedtime.entrySet()) {
            lastmodifiedtimes.setStringProperty(modifiedtime.getKey().toString(), dateFormat.format(modifiedtime.getValue()));
        }

        Propertyset metadata = impl.findPropertyset(metadataId);
        metadata.addAspect(findPropertyset(metadataAspectId));
        metadata.setComplexProperty("lastmodifiedtimes", lastmodifiedtimes);
        return metadata;
    }

    public void merge(ModelContext otherContext) {
        ModelContexts.merge(this, otherContext);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + impl.hashCode();
        result = prime * result + lastmodifiedtime.hashCode();
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

        ModelContextRecordingMetadata other = (ModelContextRecordingMetadata) obj;
        if (!impl.equals(other.impl)) {
            return false;
        }

        return lastmodifiedtime.equals(other.lastmodifiedtime);
    }

    @Override
    public String toString() {
        return "ModelContextRecordingMetadata [impl=" + impl + "]";
    }

    public void logError(String message, Object fileOrStream, Exception e) {
        impl.logError(message, fileOrStream, e);
    }

}
