/*
 * Copyright 2019 Steinar Bang
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
package no.priv.bang.authservice.sampleclient;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.mgt.eis.MemorySessionDAO;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import no.priv.bang.authservice.sampleclient.AuthserviceSampleClientShiroFilter;

public class AuthserviceSampleClientShiroFilterTest extends ShiroTestBase {

    private static MemorySessionDAO session = new MemorySessionDAO();
    private static ServletContext context;

    @BeforeAll
    public static void setup() throws Exception {
        getSecurityManager();
        context = mock(ServletContext.class);
        when(context.getContextPath()).thenReturn("/authservice");
    }

    @Test
    public void testAuthenticationSucceed() throws Exception {
        AuthserviceSampleClientShiroFilter filter = new AuthserviceSampleClientShiroFilter();
        filter.setServletContext(context);
        filter.setRealm(realm);
        filter.setSession(session);
        filter.activate();

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");
        HttpServletResponse response = mock(HttpServletResponse.class, Mockito.CALLS_REAL_METHODS);
        StringWriter bodyWriter = new StringWriter();
        PrintWriter responseWriter = new PrintWriter(bodyWriter);
        when(response.getWriter()).thenReturn(responseWriter);

        // Get the security manager from the filter and log in
        // to verify that the filter setup is working
        WebSecurityManager securitymanager = filter.getSecurityManager();
        UsernamePasswordToken token = new UsernamePasswordToken("admin", "admin".toCharArray(), true);
        AuthenticationInfo info = securitymanager.authenticate(token);
        assertEquals(1, info.getPrincipals().asList().size());
    }

}
