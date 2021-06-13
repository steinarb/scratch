/*
 * Copyright 2018-2021 Steinar Bang
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
package no.priv.bang.ukelonn.api.resources;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.Test;

import no.priv.bang.osgi.service.mocks.logservice.MockLogService;
import no.priv.bang.ukelonn.api.ServletTestBase;
import no.priv.bang.ukelonn.api.beans.LoginResult;

class LogoutTest extends ServletTestBase {

    LogoutTest() {
        super("/ukelonn", "/api");
    }

    @Test
    void testLogoutOk() throws Exception {
        // Set up the request
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();

        // Set up Shiro to be in a logged-in state
        loginUser(request, response, "jad", "1ad");

        // Create the resource and do the logout
        Logout resource = new Logout();
        resource.logservice = logservice;
        LoginResult result = resource.doLogout();

        // Check the response
        assertEquals(0, result.getRoles().length);
        assertEquals("", result.getErrorMessage());
    }

    /**
     * Verify that logging out a not-logged in shiro, is harmless.
     *
     * @throws Exception
     */
    @Test
    void testLogoutNotLoggedIn() throws Exception {
        // Set up the request and response used to do the login
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();

        // Set up shiro
        createSubjectAndBindItToThread(request, response);

        // Create the resource and do the logout
        Logout resource = new Logout();
        resource.logservice = logservice;
        LoginResult result = resource.doLogout();

        // Check the response
        assertEquals(0, result.getRoles().length);
        assertEquals("", result.getErrorMessage());
    }
}
