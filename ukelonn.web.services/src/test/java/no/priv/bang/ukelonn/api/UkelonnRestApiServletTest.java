/*
 * Copyright 2018-2022 Steinar Bang
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
package no.priv.bang.ukelonn.api;

import static no.priv.bang.ukelonn.testutils.TestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.util.ThreadContext;
import org.apache.shiro.web.subject.WebSubject;
import org.glassfish.jersey.server.ServerProperties;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.osgi.service.log.LogService;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mockrunner.mock.web.MockHttpServletRequest;
import com.mockrunner.mock.web.MockHttpServletResponse;

import no.priv.bang.osgi.service.mocks.logservice.MockLogService;
import no.priv.bang.osgiservice.users.Role;
import no.priv.bang.osgiservice.users.User;
import no.priv.bang.osgiservice.users.UserAndPasswords;
import no.priv.bang.osgiservice.users.UserManagementService;
import no.priv.bang.ukelonn.UkelonnException;
import no.priv.bang.ukelonn.UkelonnService;
import no.priv.bang.ukelonn.api.beans.AdminStatus;
import no.priv.bang.ukelonn.api.beans.LoginCredentials;
import no.priv.bang.ukelonn.api.beans.LoginResult;
import no.priv.bang.ukelonn.api.resources.ErrorMessage;
import no.priv.bang.ukelonn.backend.UkelonnServiceProvider;
import no.priv.bang.ukelonn.beans.Account;
import no.priv.bang.ukelonn.beans.AccountWithJobIds;
import no.priv.bang.ukelonn.beans.Bonus;
import no.priv.bang.ukelonn.beans.LocaleBean;
import no.priv.bang.ukelonn.beans.Notification;
import no.priv.bang.ukelonn.beans.PerformedTransaction;
import no.priv.bang.ukelonn.beans.SumYear;
import no.priv.bang.ukelonn.beans.Transaction;
import no.priv.bang.ukelonn.beans.TransactionType;
import no.priv.bang.ukelonn.beans.UpdatedTransaction;
import static no.priv.bang.ukelonn.UkelonnConstants.*;

/**
 * The tests in this test class mirrors the tests for the Jersey
 * resources.  The purpose of the tests in this test class is
 * to verify that the resources are found on the expected paths
 * and gets the expected HK2 injections and accept the
 * expected request data and returns the expected responses.
 *
 *  Sort of a lightweight integration test.
 *
 */
class UkelonnRestApiServletTest extends ServletTestBase {
    private final static Locale NB_NO = Locale.forLanguageTag("nb-no");
    private final static Locale EN_UK = Locale.forLanguageTag("en-uk");

    UkelonnRestApiServletTest() {
        super("/ukelonn", "/api");
    }

    static final ObjectMapper mapper = new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Test
    void testLoginOk() throws Exception {
        // Set up the request
        LoginCredentials credentials = LoginCredentials.with()
            .username("jad")
            .password(Base64.getEncoder().encodeToString("1ad".getBytes()))
            .build();
        MockHttpServletRequest request = buildPostUrl("/login");
        request.setBodyContent(mapper.writeValueAsString(credentials));

        // Create the response that will receive the login result
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        UkelonnService ukelonn = mock(UkelonnService.class);

        UkelonnRestApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Do the login
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        LoginResult result = ServletTestBase.mapper.readValue(response.getOutputStreamContent(), LoginResult.class);
        assertThat(result.getRoles()).isNotEmpty();
        assertEquals("", result.getErrorMessage());
    }

    @Test
    void testAdminLoginOk() throws Exception {
        // Set up the request
        LoginCredentials credentials = LoginCredentials.with()
            .username("admin")
            .password(Base64.getEncoder().encodeToString("admin".getBytes()))
            .build();
        MockHttpServletRequest request = buildPostUrl("/login");
        request.setBodyContent(mapper.writeValueAsString(credentials));

        // Create the response that will receive the login result
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        UkelonnService ukelonn = mock(UkelonnService.class);

        UkelonnRestApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Do the login
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        LoginResult result = ServletTestBase.mapper.readValue(response.getOutputStreamContent(), LoginResult.class);
        assertThat(result.getRoles()).isNotEmpty();
        assertEquals("", result.getErrorMessage());
    }

    @Disabled("Gets wrong password exception instead of unknown user exception, don't know why")
    @Test
    void testLoginUnknownUser() throws Exception {
        // Set up the request
        LoginCredentials credentials = LoginCredentials.with()
            .username("unknown")
            .password("unknown")
            .build();
        MockHttpServletRequest request = buildPostUrl("/login");
        request.setBodyContent(mapper.writeValueAsString(credentials));

        // Create the response that will receive the login result
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();
        UkelonnService ukelonn = mock(UkelonnService.class);
        UserManagementService useradmin = mock(UserManagementService.class);

        UkelonnRestApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Do the login
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        LoginResult result = ServletTestBase.mapper.readValue(response.getOutputStreamContent(), LoginResult.class);
        assertEquals(0, result.getRoles().length);
        assertEquals("Unknown account", result.getErrorMessage());
    }

    @Test
    void testLoginWrongPassword() throws Exception {
        // Set up the request
        LoginCredentials credentials = LoginCredentials.with()
            .username("jad")
            .password(Base64.getEncoder().encodeToString("wrong".getBytes()))
            .build();
        MockHttpServletRequest request = buildPostUrl("/login");
        request.setBodyContent(mapper.writeValueAsString(credentials));

        // Create the response that will receive the login result
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        UkelonnService ukelonn = mock(UkelonnService.class);

        UkelonnRestApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Do the login
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        LoginResult result = ServletTestBase.mapper.readValue(response.getOutputStreamContent(), LoginResult.class);
        assertEquals(0, result.getRoles().length);
        assertEquals("Wrong password", result.getErrorMessage());
    }

    @Test
    void testLoginWrongJson() throws Exception {
        // Set up the request
        MockHttpServletRequest request = buildPostUrl("/login");
        request.setBodyContent("xxxyzzy");

        // Create the response that will receive the login result
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        UkelonnService ukelonn = mock(UkelonnService.class);

        UkelonnRestApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Do the login
        servlet.service(request, response);

        // Check the response
        assertEquals(400, response.getStatus());
        assertEquals("text/plain", response.getContentType());
    }

    /**
     * Verify that a GET to the LoginServlet will return the current state
     * when a user is logged in
     *
     * Used to initialize webapp if the webapp is reloaded.
     *
     * @throws Exception
     */
    @Test
    void testGetLoginStateWhenLoggedIn() throws Exception {
        // Set up the request
        MockHttpServletRequest request = buildGetUrl("/login");

        // Create the response that will cause a NullPointerException
        // when trying to print the body
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        UkelonnService ukelonn = mock(UkelonnService.class);

        // Set up Shiro to be in a logged-in state
        WebSubject subject = createSubjectAndBindItToThread(request, response);
        UsernamePasswordToken token = new UsernamePasswordToken("jad", "1ad".toCharArray(), true);
        subject.login(token);

        UkelonnRestApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Check the login state with HTTP GET
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        LoginResult result = ServletTestBase.mapper.readValue(response.getOutputStreamContent(), LoginResult.class);
        assertThat(result.getRoles()).isNotEmpty();
        assertEquals("", result.getErrorMessage());
    }

    /**
     * Verify that a GET to the LoginServlet will return the current state
     * when no user is logged in
     *
     * Used to initialize webapp if the webapp is reloaded.
     *
     * @throws Exception
     */
    @Test
    void testGetLoginStateWhenNotLoggedIn() throws Exception {
        // Set up the request
        MockHttpServletRequest request = buildGetUrl("/login");

        // Create the response that will cause a NullPointerException
        // when trying to print the body
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        UkelonnService ukelonn = mock(UkelonnService.class);

        // Set up Shiro to be in a logged-in state
        WebSubject subject = createSubjectAndBindItToThread(request, response);
        subject.logout();

        UkelonnRestApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Check the login state with HTTP GET
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        LoginResult result = ServletTestBase.mapper.readValue(response.getOutputStreamContent(), LoginResult.class);
        assertEquals(0, result.getRoles().length);
        assertEquals("", result.getErrorMessage());
    }

    @Test
    void testLogoutOk() throws Exception {
        // Set up the request
        MockHttpServletRequest request = buildPostUrl("/logout");

        // Create the response that will receive the login result
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        UkelonnService ukelonn = mock(UkelonnService.class);

        UkelonnRestApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Do the logout
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        LoginResult result = ServletTestBase.mapper.readValue(response.getOutputStreamContent(), LoginResult.class);
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
        // Set up the request
        MockHttpServletRequest request = buildPostUrl("/logout");

        // Create the response that will receive the login result
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        UkelonnService ukelonn = mock(UkelonnService.class);

        // Set up shiro
        createSubjectAndBindItToThread(request, response);

        UkelonnRestApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Do the logout
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        LoginResult result = ServletTestBase.mapper.readValue(response.getOutputStreamContent(), LoginResult.class);
        assertEquals(0, result.getRoles().length);
        assertEquals("", result.getErrorMessage());
    }

    @Test
    void testGetJobtypes() throws Exception {
        // Set up the request
        MockHttpServletRequest request = buildGetUrl("/jobtypes");

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Create mock OSGi services to inject and inject it
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        UkelonnService ukelonn = mock(UkelonnService.class);
        when(ukelonn.getJobTypes()).thenReturn(getJobtypes());

        UkelonnRestApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Call the method under test
        servlet.service(request, response);

        // Check the output
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        List<TransactionType> jobtypes = mapper.readValue(getBinaryContent(response), new TypeReference<List<TransactionType>>() {});
        assertThat(jobtypes).isNotEmpty();
    }

    @Test
    void testGetAccounts() throws Exception {
        // Create the request
        MockHttpServletRequest request = buildGetUrl("/accounts");

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        UkelonnService ukelonn = mock(UkelonnService.class);
        when(ukelonn.getAccounts()).thenReturn(getDummyAccounts());

        UkelonnRestApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        List<Account> accounts = mapper.readValue(getBinaryContent(response), new TypeReference<List<Account>>() {});
        assertEquals(2, accounts.size());
    }

    @Test
    void testGetAccount() throws Exception {
        // Create the request
        MockHttpServletRequest request = buildGetUrl("/account/jad");

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        UkelonnService ukelonn = mock(UkelonnService.class);
        when(ukelonn.getAccount(anyString())).thenReturn(getJadAccount());

        UkelonnRestApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        double expectedAccountBalance = getJadAccount().getBalance();
        Account result = ServletTestBase.mapper.readValue(getBinaryContent(response), Account.class);
        assertEquals("jad", result.getUsername());
        assertEquals(expectedAccountBalance, result.getBalance(), 0.0);
    }

    /**
     * Test that verifies that a regular user can't access other users than the
     * one they are logged in as.
     *
     * @throws Exception
     */
    @Test
    void testGetAccountOtherUsername() throws Exception {
        // Create the request
        MockHttpServletRequest request = buildGetUrl("/account/jod");

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        UkelonnService ukelonn = mock(UkelonnService.class);

        UkelonnRestApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(403, response.getStatus());
    }

    /**
     * Test that verifies that an admin user can access other users than the
     * one they are logged in as.
     *
     * @throws Exception
     */
    @Test
    void testGetAccountWhenLoggedInAsAdministrator() throws Exception {
        // Create the request
        MockHttpServletRequest request = buildGetUrl("/account/jad");

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        UkelonnService ukelonn = mock(UkelonnService.class);
        when(ukelonn.getAccount(anyString())).thenReturn(getJadAccount());

        UkelonnRestApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        double expectedAccountBalance = getJadAccount().getBalance();
        Account result = ServletTestBase.mapper.readValue(response.getOutputStreamContent(), Account.class);
        assertEquals("jad", result.getUsername());
        assertEquals(expectedAccountBalance, result.getBalance(), 0.0);
    }

    @Test
    void testGetAccountNoUsername() throws Exception {
        // Create the request
        MockHttpServletRequest request = buildGetUrl("/account");

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        UkelonnService ukelonn = mock(UkelonnService.class);

        UkelonnRestApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Log the user in to shiro
        loginUser(request, response, "jad", "1ad");

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        // (Looks like Jersey enforces the pathinfo element so the response is 404 "Not Found"
        // rather than the expected 400 "Bad request" (that the resource would send if reached))
        assertEquals(404, response.getStatus());
    }

    @Test
    void testGetAccountUsernameNotPresentInDatabase() throws Exception {
        // Create the request
        MockHttpServletRequest request = buildGetUrl("/account/unknownuse");

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        UkelonnService ukelonn = mock(UkelonnService.class);
        when(ukelonn.getAccount(anyString())).thenThrow(UkelonnException.class);

        UkelonnRestApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Log the admin user in to shiro
        loginUser(request, response, "admin", "admin");

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        // (Looks like Jersey enforces the pathinfo element so the response is 404 "Not Found"
        // rather than the expected 400 "Bad request" (that the resource would send if reached))
        assertEquals(500, response.getStatus());
    }

    @Test
    void testRegisterJob() throws Exception {
        // Create the request
        Account account = getJadAccount();
        double originalBalance = account.getBalance();
        List<TransactionType> jobTypes = getJobtypes();
        PerformedTransaction job = PerformedTransaction.with()
            .account(account)
            .transactionTypeId(jobTypes.get(0).getId())
            .transactionAmount(jobTypes.get(0).getTransactionAmount())
            .transactionDate(new Date())
            .build();
        account.setBalance(account.getBalance() + jobTypes.get(0).getTransactionAmount());
        String jobAsJson = mapper.writeValueAsString(job);
        MockHttpServletRequest request = buildPostUrl("/job/register");
        request.setBodyContent(jobAsJson);

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        UkelonnService ukelonn = mock(UkelonnService.class);
        when(ukelonn.registerPerformedJob(any())).thenReturn(account);

        UkelonnRestApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        Account result = ServletTestBase.mapper.readValue(response.getOutputStreamContent(), Account.class);
        assertEquals("jad", result.getUsername());
        assertThat(result.getBalance()).isGreaterThan(originalBalance);
    }

    /**
     * Test that verifies that a regular user can't update the job list of
     * other users than the one they are logged in as.
     *
     * @throws Exception
     */
    @Test
    void testRegisterJobOtherUsername() throws Exception {
        // Create the request
        Account account = getJodAccount();
        List<TransactionType> jobTypes = getJobtypes();
        PerformedTransaction job = PerformedTransaction.with()
            .account(account)
            .transactionTypeId(jobTypes.get(0).getId())
            .transactionAmount(jobTypes.get(0).getTransactionAmount())
            .transactionDate(new Date())
            .build();
        String jobAsJson = ServletTestBase.mapper.writeValueAsString(job);
        MockHttpServletRequest request = buildPostUrl("/job/register");
        request.setBodyContent(jobAsJson);

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        UkelonnService ukelonn = mock(UkelonnService.class);

        UkelonnRestApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(403, response.getStatus());
    }

    /**
     * Test that verifies that an admin user register a job on the behalf
     * of a different user.
     *
     * @throws Exception
     */
    @Test
    void testRegisterJobtWhenLoggedInAsAdministrator() throws Exception {
        // Create the request
        Account account = getJadAccount();
        double originalBalance = account.getBalance();
        List<TransactionType> jobTypes = getJobtypes();
        PerformedTransaction job = PerformedTransaction.with()
            .account(account)
            .transactionTypeId(jobTypes.get(0).getId())
            .transactionAmount(jobTypes.get(0).getTransactionAmount())
            .transactionDate(new Date())
            .build();
        account.setBalance(account.getBalance() + jobTypes.get(0).getTransactionAmount());
        String jobAsJson = ServletTestBase.mapper.writeValueAsString(job);
        MockHttpServletRequest request = buildPostUrl("/job/register");
        request.setBodyContent(jobAsJson);

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Log the admin user in to shiro
        loginUser(request, response, "admin", "admin");

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        UkelonnService ukelonn = mock(UkelonnService.class);
        when(ukelonn.registerPerformedJob(any())).thenReturn(account);

        UkelonnRestApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());


        Account result = ServletTestBase.mapper.readValue(response.getOutputStreamContent(), Account.class);
        assertEquals("jad", result.getUsername());
        assertThat(result.getBalance()).isGreaterThan(originalBalance);
    }

    @Test
    void testRegisterJobNoUsername() throws Exception {
        // Create the request
        Account account = Account.with().build();
        List<TransactionType> jobTypes = getJobtypes();
        PerformedTransaction job = PerformedTransaction.with()
            .account(account)
            .transactionTypeId(jobTypes.get(0).getId())
            .transactionAmount(jobTypes.get(0).getTransactionAmount())
            .transactionDate(new Date())
            .build();
        String jobAsJson = ServletTestBase.mapper.writeValueAsString(job);
        MockHttpServletRequest request = buildPostUrl("/job/register");
        request.setBodyContent(jobAsJson);

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        UkelonnService ukelonn = mock(UkelonnService.class);

        UkelonnRestApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Log the user in to shiro
        loginUser(request, response, "jad", "1ad");

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(403, response.getStatus());
    }

    @Test
    void testRegisterJobUnparsablePostData() throws Exception {
        // Create the request
        MockHttpServletRequest request = buildPostUrl("/job/register");
        request.setBodyContent("this is not json");

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        UkelonnService ukelonn = mock(UkelonnService.class);

        UkelonnRestApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(400, response.getStatus());
    }

    /**
     * To provoked the internal server error, the user isn't logged in.
     * This causes a NullPointerException in the user check.
     *
     * (In a production environment this request without a login,
     * will be stopped by Shiro)
     *
     * @throws Exception
     */
    @Test
    void testRegisterJobInternalServerError() throws Exception {
        // Create the request
        Account account = Account.with().build();
        List<TransactionType> jobTypes = getJobtypes();
        PerformedTransaction job = PerformedTransaction.with()
            .account(account)
            .transactionTypeId(jobTypes.get(0).getId())
            .transactionAmount(jobTypes.get(0).getTransactionAmount())
            .transactionDate(new Date())
            .build();
        String jobAsJson = ServletTestBase.mapper.writeValueAsString(job);
        MockHttpServletRequest request = buildPostUrl("/job/register");
        request.setBodyContent(jobAsJson);

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = mock(MockHttpServletResponse.class, CALLS_REAL_METHODS);

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        UkelonnService ukelonn = mock(UkelonnService.class);

        UkelonnRestApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Clear the Subject to ensure that Shiro will fail
        // no matter what order test methods are run in
        ThreadContext.remove();

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(500, response.getStatus());
    }

    @Test
    void testGetJobs() throws Exception {
        // Set up the request
        Account account = getJadAccount();
        MockHttpServletRequest request = buildGetUrl(String.format("/jobs/%d", account.getAccountId()));

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Create mock OSGi services to inject and inject it
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        UkelonnService ukelonn = mock(UkelonnService.class);
        when(ukelonn.getJobs(anyInt())).thenReturn(getJadJobs());

        UkelonnRestApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Call the method under test
        servlet.service(request, response);

        // Check the output
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        List<Transaction> jobs = mapper.readValue(getBinaryContent(response), new TypeReference<List<Transaction>>() {});
        assertEquals(10, jobs.size());
    }

    @Test
    void testDeleteJobs() throws Exception {
        // Set up the request
        Account account = getJodAccount();
        List<Transaction> jobs = getJodJobs();
        List<Integer> jobIds = Arrays.asList(jobs.get(0).getId(), jobs.get(1).getId());
        AccountWithJobIds accountWithJobIds = AccountWithJobIds.with().account(account).jobIds(jobIds).build();
        String accountWithJobIdsAsJson = ServletTestBase.mapper.writeValueAsString(accountWithJobIds);
        MockHttpServletRequest request = buildPostUrl("/admin/jobs/delete");
        request.setBodyContent(accountWithJobIdsAsJson);

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Create mock OSGi services to inject and inject it
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        UkelonnService ukelonn = mock(UkelonnService.class);

        UkelonnRestApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Call the method under test
        servlet.service(request, response);

        // Check the output
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());
        List<Transaction> jobsAfterDelete = mapper.readValue(getBinaryContent(response), new TypeReference<List<Transaction>>() { });
        assertEquals(0, jobsAfterDelete.size());
    }

    @Test
    void testUpdateJob() throws Exception {
        // Find the job that is to be modified
        Account account = getJodAccount();
        Transaction job = getJodJobs().get(0);
        Integer originalTransactionTypeId = job.getTransactionType().getId();
        double originalTransactionAmount = job.getTransactionAmount();

        // Find a different job type that has a different amount than the
        // job's original type
        TransactionType newJobType = findJobTypeWithDifferentIdAndAmount(originalTransactionTypeId, originalTransactionAmount);

        // Create a new job object with a different jobtype and the same id
        Date now = new Date();
        UpdatedTransaction editedJob = UpdatedTransaction.with()
            .id(job.getId())
            .accountId(account.getAccountId())
            .transactionTypeId(newJobType.getId())
            .transactionTime(now)
            .transactionAmount(newJobType.getTransactionAmount())
            .build();

        // Build the HTTP request
        String editedJobAsJson = ServletTestBase.mapper.writeValueAsString(editedJob);
        MockHttpServletRequest request = buildPostUrl("/job/update");
        request.setBodyContent(editedJobAsJson);

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Create mock OSGi services to inject and inject it
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        UkelonnService ukelonn = mock(UkelonnService.class);
        when(ukelonn.updateJob(any())).thenReturn(Arrays.asList(convertUpdatedTransaction(editedJob)));

        UkelonnRestApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Call the method under test
        servlet.service(request, response);

        // Check the output (compare the updated job against the edited job values)
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());
        List<Transaction> updatedJobs = mapper.readValue(getBinaryContent(response), new TypeReference<List<Transaction>>() { });
        Transaction editedJobFromDatabase = updatedJobs.stream().filter(t->t.getId() == job.getId()).collect(Collectors.toList()).get(0);

        assertEquals(editedJob.getTransactionTypeId(), editedJobFromDatabase.getTransactionType().getId().intValue());
        assertThat(editedJobFromDatabase.getTransactionTime().getTime()).isGreaterThan(job.getTransactionTime().getTime());
        assertEquals(editedJob.getTransactionAmount(), editedJobFromDatabase.getTransactionAmount(), 0.0);
    }

    @Test
    void testGetPayments() throws Exception {
        // Set up the request
        Account account = getJadAccount();
        MockHttpServletRequest request = buildGetUrl(String.format("/payments/%d", account.getAccountId()));

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Create mock OSGi services to inject and inject it
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        UkelonnService ukelonn = mock(UkelonnService.class);
        when(ukelonn.getPayments(anyInt())).thenReturn(getJadPayments());

        UkelonnRestApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Call the method under test
        servlet.service(request, response);

        // Check the output
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        List<Transaction> payments = mapper.readValue(getBinaryContent(response), new TypeReference<List<Transaction>>() {});
        assertEquals(10, payments.size());
    }

    @Test
    void testGetPaymenttypes() throws Exception {
        // Set up the request
        MockHttpServletRequest request = buildGetUrl("/paymenttypes");

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Create mock OSGi services to inject and inject it
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        UkelonnService ukelonn = mock(UkelonnService.class);
        when(ukelonn.getPaymenttypes()).thenReturn(getPaymenttypes());

        UkelonnRestApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Call the method under test
        servlet.service(request, response);

        // Check the output
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        List<TransactionType> paymenttypes = mapper.readValue(getBinaryContent(response), new TypeReference<List<TransactionType>>() {});
        assertEquals(2, paymenttypes.size());
    }

    @Test
    void testRegisterPayments() throws Exception {
        // Create the request
        Account account = getJadAccount();
        double originalBalance = account.getBalance();
        List<TransactionType> paymentTypes = getPaymenttypes();
        PerformedTransaction payment = PerformedTransaction.with()
            .account(account)
            .transactionTypeId(paymentTypes.get(0).getId())
            .transactionAmount(paymentTypes.get(0).getTransactionAmount())
            .transactionDate(new Date())
            .build();
        account.setBalance(0.0);
        String paymentAsJson = ServletTestBase.mapper.writeValueAsString(payment);
        MockHttpServletRequest request = buildPostUrl("/registerpayment");
        request.setBodyContent(paymentAsJson);

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        UkelonnService ukelonn = mock(UkelonnService.class);
        when(ukelonn.registerPayment(any())).thenReturn(account);

        UkelonnRestApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        Account result = ServletTestBase.mapper.readValue(response.getOutputStreamContent(), Account.class);
        assertEquals("jad", result.getUsername());
        assertThat(result.getBalance()).isLessThan(originalBalance);
    }

    @Test
    void testModifyJobtype() throws Exception {
        // Find a jobtype to modify
        List<TransactionType> jobtypes = getJobtypes();
        TransactionType jobtype = jobtypes.get(0);
        Double originalAmount = jobtype.getTransactionAmount();

        // Modify the amount of the jobtype
        jobtype = TransactionType.with(jobtype).transactionAmount(originalAmount + 1).build();

        // Create the request
        String jobtypeAsJson = ServletTestBase.mapper.writeValueAsString(jobtype);
        MockHttpServletRequest request = buildPostUrl("/admin/jobtype/modify");
        request.setBodyContent(jobtypeAsJson);

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        UkelonnService ukelonn = mock(UkelonnService.class);
        when(ukelonn.modifyJobtype(any())).thenReturn(Arrays.asList(jobtype));

        UkelonnRestApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        List<TransactionType> updatedJobtypes = mapper.readValue(getBinaryContent(response), new TypeReference<List<TransactionType>>() {});
        TransactionType updatedJobtype = updatedJobtypes.get(0);
        assertThat(updatedJobtype.getTransactionAmount()).isGreaterThan(originalAmount);
    }

    @Test
    void testCreateJobtype() throws Exception {
        // Save the jobtypes before adding a new jobtype
        List<TransactionType> originalJobtypes = getJobtypes();

        // Create new jobtyoe
        TransactionType jobtype = TransactionType.with()
            .id(-1)
            .transactionTypeName("Skrubb badegolv")
            .transactionAmount(200.0)
            .transactionIsWork(true)
            .build();

        // Create the request
        String jobtypeAsJson = ServletTestBase.mapper.writeValueAsString(jobtype);
        MockHttpServletRequest request = buildPostUrl("/admin/jobtype/create");
        request.setBodyContent(jobtypeAsJson);

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        UkelonnService ukelonn = mock(UkelonnService.class);
        List<TransactionType> updatedjobtypes = Stream.concat(originalJobtypes.stream(), Stream.of(jobtype)).collect(Collectors.toList());
        when(ukelonn.createJobtype(any())).thenReturn(updatedjobtypes);

        UkelonnRestApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        // Verify that the updated have more items than the original jobtypes
        List<TransactionType> updatedJobtypes = mapper.readValue(getBinaryContent(response), new TypeReference<List<TransactionType>>() {});
        assertThat(updatedJobtypes).hasSizeGreaterThan(originalJobtypes.size());
    }

    @Test
    void testModifyPaymenttype() throws Exception {
        // Find a payment type to modify
        List<TransactionType> paymenttypes = getPaymenttypes();
        TransactionType paymenttype = paymenttypes.get(1);
        Double originalAmount = paymenttype.getTransactionAmount();

        // Modify the amount of the payment type
        paymenttype = TransactionType.with(paymenttype).transactionAmount(originalAmount + 1).build();

        // Create the request
        String paymenttypeAsJson = ServletTestBase.mapper.writeValueAsString(paymenttype);
        MockHttpServletRequest request = buildPostUrl("/admin/paymenttype/modify");
        request.setBodyContent(paymenttypeAsJson);

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        UkelonnService ukelonn = mock(UkelonnService.class);
        when(ukelonn.modifyPaymenttype(any())).thenReturn(Arrays.asList(paymenttype));

        UkelonnRestApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        List<TransactionType> updatedPaymenttypes = mapper.readValue(getBinaryContent(response), new TypeReference<List<TransactionType>>() {});
        TransactionType updatedPaymenttype = updatedPaymenttypes.get(0);
        assertThat(updatedPaymenttype.getTransactionAmount()).isGreaterThan(originalAmount);
    }

    @Test
    void testCreatePaymenttype() throws Exception {
        // Save the payment types before adding a new payment type
        List<TransactionType> originalPaymenttypes = getPaymenttypes();

        // Create new payment type
        TransactionType paymenttype = TransactionType.with()
            .id(-2)
            .transactionTypeName("Vipps")
            .transactionAmount(0.0)
            .transactionIsWagePayment(true)
            .build();

        // Create the request
        String paymenttypeAsJson = ServletTestBase.mapper.writeValueAsString(paymenttype);
        MockHttpServletRequest request = buildPostUrl("/admin/paymenttype/create");
        request.setBodyContent(paymenttypeAsJson);

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        UkelonnService ukelonn = mock(UkelonnService.class);
        List<TransactionType> updatedpaymenttypes = Stream.concat(originalPaymenttypes.stream(), Stream.of(paymenttype)).collect(Collectors.toList());
        when(ukelonn.createPaymenttype(any())).thenReturn(updatedpaymenttypes);

        UkelonnRestApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        // Verify that the updated have more items than the original jobtypes
        List<TransactionType> updatedPaymenttypes = mapper.readValue(getBinaryContent(response), new TypeReference<List<TransactionType>>() {});
        assertThat(updatedPaymenttypes).hasSizeGreaterThan(originalPaymenttypes.size());
    }

    @Test
    void testGetUsers() throws Exception {
        // Set up the request
        MockHttpServletRequest request = buildGetUrl("/users");

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Create mock OSGi services to inject and inject it
        MockLogService logservice = new MockLogService();
        UkelonnService ukelonn = mock(UkelonnService.class);
        UserManagementService useradmin = mock(UserManagementService.class);
        when(useradmin.getUsers()).thenReturn(getUsersForUserManagement());

        UkelonnRestApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Call the method under test
        servlet.service(request, response);

        // Check the output
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        List<User> users = mapper.readValue(getBinaryContent(response), new TypeReference<List<User>>() {});
        assertThat(users).isNotEmpty();
    }

    @Test
    void testModifyUser() throws Exception {
        // Get a user and modify all properties except id
        int userToModify = 0;
        List<User> users = getUsersForUserManagement();
        User userOriginal = users.get(userToModify);
        String modifiedUsername = "gandalf";
        String modifiedEmailaddress = "wizard@hotmail.com";
        String modifiedFirstname = "Gandalf";
        String modifiedLastname = "Grey";
        User user = User.with(userOriginal)
            .username(modifiedUsername)
            .email(modifiedEmailaddress)
            .firstname(modifiedFirstname)
            .lastname(modifiedLastname)
            .build();

        // Create the request
        String userAsJson = ServletTestBase.mapper.writeValueAsString(user);
        MockHttpServletRequest request = buildPostUrl("/admin/user/modify");
        request.setBodyContent(userAsJson);

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();
        UkelonnService ukelonn = mock(UkelonnService.class);
        UserManagementService useradmin = mock(UserManagementService.class);
        when(useradmin.modifyUser(any())).thenReturn(Arrays.asList(user));

        UkelonnRestApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        // Verify that the first user has the modified values
        List<User> updatedUsers = mapper.readValue(getBinaryContent(response), new TypeReference<List<User>>() {});
        User firstUser = updatedUsers.get(userToModify);
        assertEquals(modifiedUsername, firstUser.getUsername());
        assertEquals(modifiedEmailaddress, firstUser.getEmail());
        assertEquals(modifiedFirstname, firstUser.getFirstname());
        assertEquals(modifiedLastname, firstUser.getLastname());
    }

    @Test
    void testCreateUser() throws Exception {
        // Save the number of users before adding a user
        int originalUserCount = getUsers().size();

        // Create a user object
        String newUsername = "aragorn";
        String newEmailaddress = "strider@hotmail.com";
        String newFirstname = "Aragorn";
        String newLastname = "McArathorn";
        User user = User.with()
            .userid(0)
            .username(newUsername)
            .email(newEmailaddress)
            .firstname(newFirstname)
            .lastname(newLastname)
            .build();

        // Create a passwords object containing the user
        UserAndPasswords passwords = UserAndPasswords.with().user(user).password1("zecret").password2("zecret").build();

        // Create the request
        String passwordsAsJson = ServletTestBase.mapper.writeValueAsString(passwords);
        MockHttpServletRequest request = buildPostUrl("/admin/user/create");
        request.setBodyContent(passwordsAsJson);

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();
        UkelonnService ukelonn = mock(UkelonnService.class);
        UserManagementService useradmin = mock(UserManagementService.class);
        List<User> updatedusers = Stream.concat(getUsersForUserManagement().stream(), Stream.of(user)).collect(Collectors.toList());
        when(useradmin.addUser(any())).thenReturn(updatedusers);

        UkelonnRestApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        // Verify that the first user has the modified values
        List<User> updatedUsers = mapper.readValue(getBinaryContent(response), new TypeReference<List<User>>() {});

        // Verify that the last user has the expected values
        assertThat(updatedUsers).hasSizeGreaterThan(originalUserCount);
        User lastUser = updatedUsers.get(updatedUsers.size() - 1);
        assertEquals(newUsername, lastUser.getUsername());
        assertEquals(newEmailaddress, lastUser.getEmail());
        assertEquals(newFirstname, lastUser.getFirstname());
        assertEquals(newLastname, lastUser.getLastname());
    }

    @Test
    void testChangePassword() throws Exception {
        List<User> users = getUsersForUserManagement();

        // Save the number of users before adding a user
        int originalUserCount = users.size();

        // Get a user with a valid username
        User user = users.get(1);

        // Create a passwords object containing the user and with valid passwords
        UserAndPasswords passwords = UserAndPasswords.with().user(user).password1("zecret").password2("zecret").build();

        // Create the request
        String passwordsAsJson = ServletTestBase.mapper.writeValueAsString(passwords);
        MockHttpServletRequest request = buildPostUrl("/admin/user/password");
        request.setBodyContent(passwordsAsJson);

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Create mock OSGi services to inject
        MockLogService logservice = new MockLogService();
        UkelonnService ukelonn = mock(UkelonnService.class);
        UserManagementService useradmin = mock(UserManagementService.class);
        when(useradmin.updatePassword(any())).thenReturn(users);

        UkelonnRestApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        List<User> updatedUsers = mapper.readValue(getBinaryContent(response), new TypeReference<List<User>>() {});

        // Verify that the number of users hasn't changed
        assertEquals(originalUserCount, updatedUsers.size());
    }

    @Test
    void testStatisticsEarningsSumOverYear() throws Exception {
        // Set up REST API servlet with mocked services
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        UkelonnService ukelonn = mock(UkelonnService.class);
        List<SumYear> earningsSumOverYear = Arrays.asList(
            SumYear.with().sum(1250.0).year(2016).build(),
            SumYear.with().sum(2345.0).year(2017).build(),
            SumYear.with().sum(5467.0).year(2018).build(),
            SumYear.with().sum(2450.0).year(2019).build());
        when(ukelonn.earningsSumOverYear("jad")).thenReturn(earningsSumOverYear);

        UkelonnRestApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Create the request and response
        MockHttpServletRequest request = buildGetUrl("/statistics/earnings/sumoveryear/jad");
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        List<SumYear> statistics = mapper.readValue(getBinaryContent(response), new TypeReference<List<SumYear>>() {});

        // Verify that the number of users hasn't changed
        assertEquals(4, statistics.size());
    }

    @Test
    void testNotifications() throws Exception {
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        UkelonnService ukelonn = new UkelonnServiceProvider();

        UkelonnRestApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // A request for notifications to a user
        MockHttpServletRequest requestGetNotifications = buildGetUrl("/notificationsto/jad");

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse notificationsResponse = new MockHttpServletResponse();

        // Do a REST API call
        servlet.service(requestGetNotifications, notificationsResponse);

        // Check the REST API response (no notifications expected)
        assertEquals(200, notificationsResponse.getStatus());
        assertEquals("application/json", notificationsResponse.getContentType());
        List<User> notificationsToJad = mapper.readValue(getBinaryContent(notificationsResponse), new TypeReference<List<User>>() {});
        assertThat(notificationsToJad).isEmpty();

        // Send a notification to user "jad" over the REST API
        Notification utbetalt = Notification.with().title("Ukelønn").message("150 kroner utbetalt til konto").build();
        String utbetaltAsJson = mapper.writeValueAsString(utbetalt);
        MockHttpServletRequest sendNotificationRequest = buildPostUrl("/notificationto/jad");
        sendNotificationRequest.setBodyContent(utbetaltAsJson);
        MockHttpServletResponse sendNotificationResponse = new MockHttpServletResponse();
        servlet.service(sendNotificationRequest, sendNotificationResponse);

        if (sendNotificationResponse.getStatus() == HttpServletResponse.SC_BAD_REQUEST) {
            System.err.println("Error in POST request: " + sendNotificationResponse.getOutputStreamContent());
        }

        // A new REST API request for notifications to "jad" will return a single notification
        MockHttpServletResponse notificationsResponse2 = new MockHttpServletResponse();
        servlet.service(requestGetNotifications, notificationsResponse2);
        assertEquals(200, notificationsResponse2.getStatus());
        assertEquals("application/json", notificationsResponse2.getContentType());
        List<Notification> notificationsToJad2 = mapper.readValue(getBinaryContent(notificationsResponse2), new TypeReference<List<Notification>>() {});
        assertEquals(utbetalt.getTitle(), notificationsToJad2.get(0).getTitle());
        assertEquals(utbetalt.getMessage(), notificationsToJad2.get(0).getMessage());
    }

    @Test
    void testGetActiveBonuses() throws Exception {
        // Set up REST API servlet with mocked services
        UkelonnService ukelonn = mock(UkelonnService.class);
        when(ukelonn.getActiveBonuses()).thenReturn(Collections.singletonList(Bonus.with().build()));

        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);

        UkelonnRestApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Create the request and response
        MockHttpServletRequest request = buildGetUrl("/activebonuses");
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());
        List<Bonus> activeBonuses = mapper.readValue(getBinaryContent(response), new TypeReference<List<Bonus>>() {});
        assertThat(activeBonuses).isNotEmpty();
    }

    @Test
    void testGetAllBonuses() throws Exception {
        // Set up REST API servlet with mocked services
        UkelonnService ukelonn = mock(UkelonnService.class);
        when(ukelonn.getAllBonuses()).thenReturn(Collections.singletonList(Bonus.with().build()));

        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);

        UkelonnRestApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Create the request and response
        MockHttpServletRequest request = buildGetUrl("/allbonuses");
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());
        List<Bonus> allBonuses = mapper.readValue(getBinaryContent(response), new TypeReference<List<Bonus>>() {});
        assertThat(allBonuses).isNotEmpty();
    }

    @Test
    void testPostCreateBonus() throws Exception {
        // Set up REST API servlet with mocked services
        Bonus bonus = Bonus.with()
            .bonusId(1)
            .enabled(true)
            .title("Julebonus")
            .description("Dobbelt lønn for jobb")
            .bonusFactor(2.0)
            .startDate(new Date())
            .endDate(new Date())
            .build();
        UkelonnService ukelonn = mock(UkelonnService.class);
        when(ukelonn.createBonus(bonus)).thenReturn(Collections.singletonList(bonus));

        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);

        UkelonnRestApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Create the request and response
        MockHttpServletRequest request = buildPostUrl("/admin/createbonus");
        String postBody = mapper.writeValueAsString(bonus);
        request.setBodyContent(postBody);
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());
        List<Bonus> bonusesWithAddedBonus = mapper.readValue(getBinaryContent(response), new TypeReference<List<Bonus>>() {});
        assertThat(bonusesWithAddedBonus).contains(bonus);
    }

    @Test
    void testPostUpdateBonus() throws Exception {
        // Set up REST API servlet with mocked services
        Bonus bonus = Bonus.with()
            .bonusId(1)
            .enabled(true)
            .title("Julebonus")
            .description("Dobbelt lønn for jobb")
            .bonusFactor(2.0)
            .startDate(new Date())
            .endDate(new Date())
            .build();
        UkelonnService ukelonn = mock(UkelonnService.class);
        when(ukelonn.modifyBonus(bonus)).thenReturn(Collections.singletonList(bonus));

        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);

        UkelonnRestApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Create the request and response
        MockHttpServletRequest request = buildPostUrl("/admin/modifybonus");
        String postBody = mapper.writeValueAsString(bonus);
        request.setBodyContent(postBody);
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());
        List<Bonus> bonusesWithUpdatedBonus = mapper.readValue(getBinaryContent(response), new TypeReference<List<Bonus>>() {});
        assertThat(bonusesWithUpdatedBonus).contains(bonus);
    }

    @Test
    void testPostDeleteBonus() throws Exception {
        // Set up REST API servlet with mocked services
        Bonus bonus = Bonus.with()
            .bonusId(1)
            .enabled(true)
            .title("Julebonus")
            .description("Dobbelt lønn for jobb")
            .bonusFactor(2.0)
            .startDate(new Date())
            .endDate(new Date())
            .build();
        UkelonnService ukelonn = mock(UkelonnService.class);
        when(ukelonn.deleteBonus(bonus)).thenReturn(Collections.singletonList(Bonus.with().build()));

        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);

        UkelonnRestApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Create the request and response
        MockHttpServletRequest request = buildPostUrl("/admin/deletebonus");
        String postBody = mapper.writeValueAsString(bonus);
        request.setBodyContent(postBody);
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());
        List<Bonus> bonusesWithDeletedBonus = mapper.readValue(getBinaryContent(response), new TypeReference<List<Bonus>>() {});
        assertThat(bonusesWithDeletedBonus)
            .isNotEmpty()
            .doesNotContain(bonus);
    }

    @Test
    void testPostAdminStatus() throws Exception {
        // Set up REST API servlet with mocked services
        UkelonnService ukelonn = mock(UkelonnService.class);
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        Role adminrole = Role.with().id(1).rolename(UKELONNADMIN_ROLE).description("ukelonn adminstrator").build();
        when(useradmin.getRolesForUser(anyString())).thenReturn(Collections.singletonList(adminrole));

        UkelonnRestApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Create a user object
        String newUsername = "aragorn";
        String newEmailaddress = "strider@hotmail.com";
        String newFirstname = "Aragorn";
        String newLastname = "McArathorn";
        User user = User.with()
            .userid(0)
            .username(newUsername)
            .email(newEmailaddress)
            .firstname(newFirstname)
            .lastname(newLastname)
            .build();

        // Create the request and response
        MockHttpServletRequest request = buildPostUrl("/admin/user/adminstatus");
        String postBody = mapper.writeValueAsString(user);
        request.setBodyContent(postBody);
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());
        AdminStatus updatedStatus = mapper.readValue(getBinaryContent(response), AdminStatus.class);
        assertEquals(user, updatedStatus.getUser());
        assertTrue(updatedStatus.isAdministrator());
    }

    @Test
    void testPostChangeAdminStatus() throws Exception {
        // Set up REST API servlet with mocked services
        UkelonnService ukelonn = mock(UkelonnService.class);
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        Role adminrole = Role.with().id(1).rolename(UKELONNADMIN_ROLE).description("ukelonn adminstrator").build();
        when(useradmin.getRolesForUser(anyString())).thenReturn(Collections.singletonList(adminrole));

        UkelonnRestApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Create a user object
        String newUsername = "aragorn";
        String newEmailaddress = "strider@hotmail.com";
        String newFirstname = "Aragorn";
        String newLastname = "McArathorn";
        User user = User.with()
            .userid(0)
            .username(newUsername)
            .email(newEmailaddress)
            .firstname(newFirstname)
            .lastname(newLastname)
            .build();
        AdminStatus status = AdminStatus.with().user(user).administrator(true).build();

        // Create the request and response
        MockHttpServletRequest request = buildPostUrl("/admin/user/changeadminstatus");
        String postBody = mapper.writeValueAsString(status);
        request.setBodyContent(postBody);
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());
        AdminStatus updatedStatus = mapper.readValue(getBinaryContent(response), AdminStatus.class);
        assertEquals(user, updatedStatus.getUser());
        assertTrue(updatedStatus.isAdministrator());
    }

    @Test
    void testDefaultLocale() throws Exception {
        // Set up REST API servlet with mocked services
        UkelonnService ukelonn = mock(UkelonnService.class);
        when(ukelonn.defaultLocale()).thenReturn(NB_NO);
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);

        UkelonnRestApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Create the request and response
        MockHttpServletRequest request = buildGetUrl("/defaultlocale");
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());
        Locale defaultLocale = mapper.readValue(getBinaryContent(response), Locale.class);
        assertEquals(NB_NO, defaultLocale);
    }

    @Test
    void testAvailableLocales() throws Exception {
        // Set up REST API servlet with mocked services
        UkelonnService ukelonn = mock(UkelonnService.class);
        when(ukelonn.availableLocales()).thenReturn(Collections.singletonList(Locale.forLanguageTag("nb-NO")).stream().map(l -> LocaleBean.with().locale(l).build()).collect(Collectors.toList()));
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);

        UkelonnRestApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Create the request and response
        MockHttpServletRequest request = buildGetUrl("/availablelocales");
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());
        List<LocaleBean> availableLocales = mapper.readValue(getBinaryContent(response), new TypeReference<List<LocaleBean>>() {});
        assertThat(availableLocales).isNotEmpty().contains(LocaleBean.with().locale(Locale.forLanguageTag("nb-NO")).build());
    }

    @Test
    void testDisplayTexts() throws Exception {
        // Set up REST API servlet with mocked services
        UkelonnService ukelonn = mock(UkelonnService.class);
        Map<String, String> texts = new HashMap<>();
        texts.put("date", "Dato");
        when(ukelonn.displayTexts(NB_NO)).thenReturn(texts);
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);

        UkelonnRestApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Create the request and response
        MockHttpServletRequest request = buildGetUrl("/displaytexts");
        request.setQueryString("locale=nb_NO");
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());
        Map<String, String> displayTexts = mapper.readValue(getBinaryContent(response), new TypeReference<Map<String, String>>() {});
        assertThat(displayTexts).isNotEmpty();
    }

    @Test
    void testDisplayTextsWithUnknownLocale() throws Exception {
        // Set up REST API servlet with mocked services
        UkelonnService ukelonn = mock(UkelonnService.class);
        Map<String, String> texts = new HashMap<>();
        texts.put("date", "Dato");
        when(ukelonn.displayTexts(EN_UK)).thenThrow(MissingResourceException.class);
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);

        UkelonnRestApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(ukelonn, logservice, useradmin);

        // Create the request and response
        MockHttpServletRequest request = buildGetUrl("/displaytexts");
        request.setQueryString("locale=en_UK");
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(500, response.getStatus());
        assertEquals("application/json", response.getContentType());
        ErrorMessage errorMessage = mapper.readValue(getBinaryContent(response), ErrorMessage.class);
        assertEquals(500, errorMessage.getStatus());
        assertThat(errorMessage.getMessage()).startsWith("Unknown locale");
    }

    private TransactionType findJobTypeWithDifferentIdAndAmount(Integer transactionTypeId, double amount) {
        return getJobtypes().stream().filter(t->!t.getId().equals(transactionTypeId)).filter(t->t.getTransactionAmount() != amount).collect(Collectors.toList()).get(0);
    }


    private UkelonnRestApiServlet simulateDSComponentActivationAndWebWhiteboardConfiguration(UkelonnService ukelonn, LogService logservice, UserManagementService useradmin) throws Exception {
        UkelonnRestApiServlet servlet = new UkelonnRestApiServlet();
        servlet.setLogService(logservice);
        servlet.setUkelonnService(ukelonn);
        servlet.setUserManagement(useradmin);
        ServletConfig config = createServletConfigWithApplicationAndPackagenameForJerseyResources();
        servlet.init(config);
        return servlet;
    }

    private ServletConfig createServletConfigWithApplicationAndPackagenameForJerseyResources() {
        ServletConfig config = mock(ServletConfig.class);
        when(config.getInitParameterNames()).thenReturn(Collections.enumeration(Arrays.asList(ServerProperties.PROVIDER_PACKAGES)));
        when(config.getInitParameter(ServerProperties.PROVIDER_PACKAGES)).thenReturn("no.priv.bang.ukelonn.api.resources");
        ServletContext servletContext = mock(ServletContext.class);
        when(servletContext.getContextPath()).thenReturn("/ukelonn");
        when(config.getServletContext()).thenReturn(servletContext);
        when(servletContext.getAttributeNames()).thenReturn(Collections.emptyEnumeration());
        return config;
    }

}
