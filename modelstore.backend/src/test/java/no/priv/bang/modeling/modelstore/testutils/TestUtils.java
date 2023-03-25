package no.priv.bang.modeling.modelstore.testutils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import no.priv.bang.modeling.modelstore.services.ModelContext;
import no.priv.bang.modeling.modelstore.services.Propertyset;
import no.priv.bang.modeling.modelstore.services.Value;

/**
 * Contains static methods used in more than one unit test.
 *
 * @author Steinar Bang
 *
 */
public class TestUtils {

    /**
     * Get a {@link File} referencing a resource.
     *
     * @param resource the name of the resource to get a File for
     * @return a {@link File} object referencing the resource
     * @throws URISyntaxException
     */
    public static File getResourceAsFile(String resource) throws URISyntaxException {
        return Paths.get(TestUtils.class.getResource(resource).toURI()).toFile();
    }

    /**
     * Iterate over all of the {@link Propertyset} instances of a
     * {@link Modelstore} and compare them to the propertysets
     * of a different Modelstore and assert that they match.
     *
     * @param context the {@link ModelContext} to iterate over
     * @param context2 the {@link ModelContext} to compare with
     */
    public static void compareAllPropertysets(ModelContext context, ModelContext context2) {
        for (Propertyset propertyset : context.listAllPropertysets()) {
            Propertyset parsedPropertyset = context2.findPropertyset(propertyset.getId());
            for (String propertyname : propertyset.getPropertynames()) {
                Value originalValue = propertyset.getProperty(propertyname);
                Value parsedValue = parsedPropertyset.getProperty(propertyname);
                assertEquals(originalValue, parsedValue);
            }
        }
    }

}
