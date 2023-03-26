package no.priv.bang.modeling.modelstore.backend;

import static no.priv.bang.modeling.modelstore.backend.Values.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonFactory;

import no.priv.bang.modeling.modelstore.services.ModelContext;
import no.priv.bang.modeling.modelstore.services.Modelstore;
import no.priv.bang.modeling.modelstore.services.Propertyset;
import no.priv.bang.modeling.modelstore.services.Value;
import no.priv.bang.modeling.modelstore.services.ValueList;


public class ModelContextImpl implements ModelContext {

    protected final UUID metadataId = UUID.fromString("b1ad694b-4003-412b-8249-a7d1a0a24cf3");
    private Map<UUID, Propertyset> propertysets = new HashMap<>();
    private Set<Propertyset> embeddedAspects;
    private Modelstore modelstore; // Only used for logging

    public ModelContextImpl() {
        this(null);
    }

    public ModelContextImpl(Modelstore modelstore) {
        this.modelstore = modelstore;
        loadEmbeddedAspects();
    }

    /* (non-Javadoc)
     * @see no.priv.bang.modeling.modelstore.ModelContext#createPropertyset()
     */
    public Propertyset createPropertyset() {
        return new PropertysetImpl();
    }

    /* (non-Javadoc)
     * @see no.priv.bang.modeling.modelstore.ModelContext#createList()
     */
    public ValueList createList() {
        return newList();
    }

    /* (non-Javadoc)
     * @see no.priv.bang.modeling.modelstore.ModelContext#findPropertyset(java.util.UUID)
     */
    public Propertyset findPropertyset(UUID id) {
        return propertysets.computeIfAbsent(id, PropertysetImpl::new);
    }

    /* (non-Javadoc)
     * @see no.priv.bang.modeling.modelstore.ModelContext#listAllPropertysets()
     */
    public Collection<Propertyset> listAllPropertysets() {
        List<Propertyset> allPropertysetsExcludingEmbedded = new ArrayList<>(propertysets.size());
        for (Propertyset propertyset : propertysets.values()) {
            if (!embeddedAspects.contains(propertyset)) {
                allPropertysetsExcludingEmbedded.add(propertyset);
            }
        }

        return allPropertysetsExcludingEmbedded;
    }

    /* (non-Javadoc)
     * @see no.priv.bang.modeling.modelstore.ModelContext#listAllAspects()
     */
    public Collection<Propertyset> listAllAspects() {
        Set<Propertyset> allAspects = new HashSet<>(embeddedAspects);
        for (Entry<UUID, Propertyset> propertyset : propertysets.entrySet()) {
            if (propertyset.getValue().hasAspect()) {
                ValueList aspects = propertyset.getValue().getAspects();
                for (Value value : aspects) {
                    Propertyset aspect = value.asReference();
                    if (!getNilPropertyset().equals(aspect)) {
                        allAspects.add(aspect);
                        Propertyset baseAspect = aspect.getReferenceProperty("inherits");
                        if (!getNilPropertyset().equals(baseAspect)) {
                            allAspects.add(baseAspect);
                        }
                    }
                }
            }
        }

        return allAspects;
    }

    /* (non-Javadoc)
     * @see no.priv.bang.modeling.modelstore.ModelContext#findObjectsOfAspect(no.priv.bang.modeling.modelstore.Propertyset)
     */
    public Collection<Propertyset> findObjectsOfAspect(Propertyset aspect) {
        List<Propertyset> objectsOfAspect = new ArrayList<>();
        for (Entry<UUID, Propertyset> propertysetEntry : propertysets.entrySet()) {
            ValueList aspectList = propertysetEntry.getValue().getListProperty("aspects");
            for (Value aspectValue : aspectList) {
                Set<Propertyset> aspectInheritanceChain = followInheritanceChain(aspectValue.asReference());
                if (aspectInheritanceChain.contains(aspect)) {
                    objectsOfAspect.add(propertysetEntry.getValue());
                }
            }
        }

        return objectsOfAspect;
    }

    private void loadEmbeddedAspects() {
        try {
            InputStream aspectsFile = getClass().getResourceAsStream("/json/aspects.json");
            JsonFactory jsonFactory = new JsonFactory();
            JsonPropertysetPersister persister = new JsonPropertysetPersister(jsonFactory);
            persister.restore(aspectsFile, this);
            embeddedAspects = new HashSet<>(propertysets.values());
        } catch (Exception e) { /* skip and continue */ }
    }

    Set<Propertyset> followInheritanceChain(Propertyset aspect) {
        Propertyset baseAspect = aspect.getReferenceProperty("inherits");
        if (!getNilPropertyset().equals(baseAspect)) {
            Set<Propertyset> aspects = followInheritanceChain(baseAspect);
            aspects.add(aspect);
            return aspects;
        } else {
            // No more base aspects, create the set and add myself
            Set<Propertyset> aspects = new HashSet<>();
            aspects.add(aspect);
            return aspects;
        }
    }

    public Date getLastmodifieddate(Propertyset propertyset) {
        // This class does not store metadata for its propertysets, just give the current time
        return new Date();
    }

    public void merge(ModelContext otherContext) {
        ModelContexts.merge(this, otherContext);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + propertysets.hashCode();
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

        ModelContextImpl other = (ModelContextImpl) obj;
        return propertysets.equals(other.propertysets);
    }

    @Override
    public String toString() {
        return "ModelContextImpl [propertysets=" + propertysets + "]";
    }

    public void logError(String message, Object fileOrStream, Exception e) {
        modelstore.logError(message, fileOrStream, e);
    }

}
