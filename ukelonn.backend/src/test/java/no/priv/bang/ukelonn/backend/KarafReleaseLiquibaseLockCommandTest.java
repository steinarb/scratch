/*
 * Copyright 2016-2021 Steinar Bang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations
 * under the License.
 */
package no.priv.bang.ukelonn.backend;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;

import static no.priv.bang.ukelonn.testutils.TestUtils.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.wiring.BundleWiring;

import no.priv.bang.ukelonn.UkelonnService;
import no.priv.bang.ukelonn.testutils.TestUtils;

/*
 * Unit tests for {@link KarafReleaseLiquibaseLockCommand}.
 */
class KarafReleaseLiquibaseLockCommandTest {

    private BundleContext bundleContext;

    @BeforeAll
    static void setupForAllTests() throws Exception {
        setupFakeOsgiServices();
    }

    @AfterAll
    static void teardownForAllTests() throws Exception {
        releaseFakeOsgiServices();
    }

    @BeforeEach
    void setup() {
        var bundleWiring = mock(BundleWiring.class);
        when(bundleWiring.getClassLoader()).thenReturn(TestUtils.class.getClassLoader());
        var bundle = mock(Bundle.class);
        when(bundle.adapt(BundleWiring.class)).thenReturn(bundleWiring);
        bundleContext = mock(BundleContext.class);
        when(bundleContext.getBundle()).thenReturn(bundle);
    }

    /*
     * If no {@link UkelonnDatabase} OSGi service is present
     * a NullPointerException will abort the command.
     */
    @Test
    void testExecuteNullDatabaseOsgiService() throws Exception {
        KarafReleaseLiquibaseLockCommand action = new KarafReleaseLiquibaseLockCommand();
        try {
            action.execute();
            fail("Expected a NullPointerExceptio to be thrown");
        } catch (NullPointerException e) {
        }
    }

    @Test
    void testExecute() throws Exception {
        KarafReleaseLiquibaseLockCommand action = new KarafReleaseLiquibaseLockCommand();
        UkelonnService ukelonn = getUkelonnServiceSingleton();

        // Fake OSGi service injection
        setInternalState(action, "ukelonn", ukelonn);

        // Fake BundleContext injection
        setInternalState(action, "bundleContext", bundleContext);

        // Run the code under test
        Object result = action.execute();

        // Verify expected results
        assertNull(result);
    }

    private void setInternalState(Object object, String fieldName, Object value) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.set(object, value);
    }

}
