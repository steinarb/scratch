package no.priv.bang.modeling.modelstore.backend;

import static no.priv.bang.modeling.modelstore.backend.ModelContexts.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import no.priv.bang.modeling.modelstore.services.ModelContext;

/**
 * Unit tests for {@link ModelContexts}
 *
 * @author Steinar Bang
 *
 */
public class ModelContextsTest {

    /**
     * Unit test for {@link ModelContexts#findWrappedModelContext(no.priv.bang.modeling.modelstore.ModelContext)}.
     */
    @Test
    public void testFindWrappedModelContext() {
        ModelContext inner = new ModelContextImpl();
        ModelContext context = new ModelContextRecordingMetadata(inner);
        assertSame(inner, findWrappedModelContext(context));
        assertSame(inner, findWrappedModelContext(inner));
    }

}
