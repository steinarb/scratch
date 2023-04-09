package no.priv.bang.modeling.modelstore.backend;

import static no.priv.bang.modeling.modelstore.backend.ModelContexts.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import no.priv.bang.modeling.modelstore.services.ModelContext;
import no.priv.bang.modeling.modelstore.services.Modelstore;
import no.priv.bang.modeling.modelstore.value.ValueCreatorProvider;

/**
 * Unit tests for {@link ModelContexts}
 *
 */
class ModelContextsTest {

    /**
     * Unit test for {@link ModelContexts#findWrappedModelContext(no.priv.bang.modeling.modelstore.ModelContext)}.
     */
    @Test
    void testFindWrappedModelContext() {
        var modelstore = mock(Modelstore.class);
        var valueCreator = new ValueCreatorProvider();
        when(modelstore.getValueCreator()).thenReturn(valueCreator);
        ModelContext inner = new ModelContextImpl(modelstore);
        ModelContext context = new ModelContextRecordingMetadata(inner, null, null);
        assertSame(inner, findWrappedModelContext(context));
        assertSame(inner, findWrappedModelContext(inner));
    }

}
