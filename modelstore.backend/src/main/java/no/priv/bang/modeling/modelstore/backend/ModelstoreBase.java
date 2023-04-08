package no.priv.bang.modeling.modelstore.backend;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.core.JsonFactory;

import no.priv.bang.modeling.modelstore.services.DateFactory;
import no.priv.bang.modeling.modelstore.services.ErrorBean;
import no.priv.bang.modeling.modelstore.services.ModelContext;
import no.priv.bang.modeling.modelstore.services.Modelstore;
import no.priv.bang.modeling.modelstore.services.ValueCreator;

/**
 * Class implementing Modelstore for use as a base
 * class for Provider classes for Modelstore.
 *
 */
class ModelstoreBase extends BuiltinAspectsBase implements Modelstore {

    private ModelContext context = null;
    private List<ErrorBean> errors = Collections.synchronizedList(new ArrayList<ErrorBean>());
    private DateFactory dateFactory = Date::new;
    private ValueCreator valueCreator;

    protected ModelstoreBase() {
    }

    public void setDateFactory(DateFactory dateFactory) {
        this.dateFactory = dateFactory;
    }

    protected void doSetValueCreator(ValueCreator valueCreator) {
        this.valueCreator = valueCreator;
    }

    protected void doActivate() {
        context = new ModelContextImpl(this);
    }

    public ModelContext getDefaultContext() {
        return context;
    }

    public ModelContext createContext() {
        ModelContextImpl ctxt = new ModelContextImpl(this);
        return new ModelContextRecordingMetadata(ctxt, dateFactory, valueCreator);
    }

    public ModelContext restoreContext(InputStream jsonfilestream) {
        ModelContextImpl ctxt = new ModelContextImpl(this);
        JsonFactory jsonFactory = new JsonFactory();
        JsonPropertysetPersister persister = new JsonPropertysetPersister(jsonFactory, null);
        persister.restore(jsonfilestream, ctxt);

        return new ModelContextRecordingMetadata(ctxt, dateFactory, valueCreator);
    }

    public void persistContext(OutputStream jsonfilestream, ModelContext context) {
        JsonFactory jsonFactory = new JsonFactory();
        JsonPropertysetPersister persister = new JsonPropertysetPersister(jsonFactory, null);
        persister.persist(jsonfilestream, context);
    }

    public void logError(String message, Object fileOrStream, Exception execption) {
        errors.add(new ErrorBean(new Date(), message, fileOrStream, execption));
    }

    public List<ErrorBean> getErrors() {
        synchronized (errors) {
            // Defensive copy
            return new ArrayList<>(errors);
        }
    }

    @Override
    public ValueCreator getValueCreator() {
        return valueCreator;
    }

}
