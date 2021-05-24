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

import static no.priv.bang.ukelonn.testutils.TestUtils.*;
import static org.junit.Assert.*;
import static org.junit.Assert.fail;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.InternalServerErrorException;

import org.apache.shiro.util.ThreadContext;
import org.junit.Test;

import no.priv.bang.osgi.service.mocks.logservice.MockLogService;
import no.priv.bang.ukelonn.UkelonnService;
import no.priv.bang.ukelonn.api.ServletTestBase;
import no.priv.bang.ukelonn.backend.UkelonnServiceProvider;
import no.priv.bang.ukelonn.beans.Account;
import no.priv.bang.ukelonn.beans.PerformedTransaction;
import no.priv.bang.ukelonn.beans.Transaction;
import no.priv.bang.ukelonn.beans.TransactionType;
import no.priv.bang.ukelonn.beans.UpdatedTransaction;

public class JobResourceTest extends ServletTestBase {

    public JobResourceTest() {
        super("/ukelonn", "/api");
    }

    @Test
    public void testRegisterJob() throws Exception {
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

        // Create the request and response for the Shiro login
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Create the object to be tested
        JobResource resource = new JobResource();

        // Log the user in to shiro
        loginUser(request, response, "jad", "1ad");

        // Create mock OSGi services to inject and inject it
        MockLogService logservice = new MockLogService();
        resource.setLogservice(logservice);

        // Inject fake OSGi service UkelonnService
        UkelonnService ukelonn = mock(UkelonnService.class);
        Account accountWithUpdatedBalance = copyAccount(account);
        accountWithUpdatedBalance.setBalance(account.getBalance() + job.getTransactionAmount());
        when(ukelonn.registerPerformedJob(any())).thenReturn(accountWithUpdatedBalance);
        resource.ukelonn = ukelonn;

        // Run the method under test
        Account result = resource.doRegisterJob(job);

        // Check the response
        assertEquals("jad", result.getUsername());
        assertThat(result.getBalance()).isGreaterThan(originalBalance);
    }

    /**
     * Test that verifies that a regular user can't update the job list of
     * other users than the one they are logged in as.
     *
     * @throws Exception
     */
    @Test(expected=ForbiddenException.class)
    public void testRegisterJobOtherUsername() throws Exception {
        // Create the request
        Account account = getJodAccount();
        List<TransactionType> jobTypes = getJobtypes();
        PerformedTransaction job = PerformedTransaction.with()
            .account(account)
            .transactionTypeId(jobTypes.get(0).getId())
            .transactionAmount(jobTypes.get(0).getTransactionAmount())
            .transactionDate(new Date())
            .build();

        // Create the request and response for the Shiro login
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Log the user in to shiro
        loginUser(request, response, "jad", "1ad");

        // Create the object to be tested
        JobResource resource = new JobResource();

        // Create mock OSGi services to inject and inject it
        MockLogService logservice = new MockLogService();
        resource.setLogservice(logservice);

        // Inject fake OSGi service UkelonnService
        UkelonnService ukelonn = mock(UkelonnService.class);
        resource.ukelonn = ukelonn;

        // Run the method under test
        resource.doRegisterJob(job);
    }

    /**
     * Test that verifies that an admin user register a job on the behalf
     * of a different user.
     *
     * @throws Exception
     */
    @Test
    public void testRegisterJobtWhenLoggedInAsAdministrator() throws Exception {
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

        // Create the request and response for the Shiro login
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Log the admin user in to shiro
        loginUser(request, response, "admin", "admin");

        // Create the object to be tested
        JobResource resource = new JobResource();

        // Create mock OSGi services to inject and inject it
        MockLogService logservice = new MockLogService();
        resource.setLogservice(logservice);

        // Inject fake OSGi service UkelonnService
        UkelonnService ukelonn = mock(UkelonnService.class);
        Account accountWithUpdatedBalance = copyAccount(account);
        accountWithUpdatedBalance.setBalance(account.getBalance() + job.getTransactionAmount());
        when(ukelonn.registerPerformedJob(any())).thenReturn(accountWithUpdatedBalance);
        resource.ukelonn = ukelonn;

        // Run the method under test
        Account result = resource.doRegisterJob(job);

        // Check the response
        assertEquals("jad", result.getUsername());
        assertThat(result.getBalance()).isGreaterThan(originalBalance);
    }

    @Test(expected=ForbiddenException.class)
    public void testRegisterJobNoUsername() throws Exception {
        // Create the request
        Account account = Account.with().build();
        List<TransactionType> jobTypes = getJobtypes();
        PerformedTransaction job = PerformedTransaction.with()
            .account(account)
            .transactionTypeId(jobTypes.get(0).getId())
            .transactionAmount(jobTypes.get(0).getTransactionAmount())
            .transactionDate(new Date())
            .build();

        // Create the request and response for the Shiro login
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Log the user in to shiro
        loginUser(request, response, "jad", "1ad");

        JobResource resource = new JobResource();

        // Create mock OSGi services to inject and inject it
        MockLogService logservice = new MockLogService();
        resource.setLogservice(logservice);

        // Inject fake OSGi service UkelonnService
        UkelonnService ukelonn = mock(UkelonnService.class);
        resource.ukelonn = ukelonn;

        // Run the method under test
        resource.doRegisterJob(job);
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
    @Test(expected=InternalServerErrorException.class)
    public void testRegisterJobInternalServerError() throws Exception {
        // Create the request
        Account account = Account.with().build();
        List<TransactionType> jobTypes = getJobtypes();
        PerformedTransaction job = PerformedTransaction.with()
            .account(account)
            .transactionTypeId(jobTypes.get(0).getId())
            .transactionAmount(jobTypes.get(0).getTransactionAmount())
            .transactionDate(new Date())
            .build();

        // Create the object to be tested
        JobResource resource = new JobResource();

        // Create mock OSGi services to inject and inject it
        MockLogService logservice = new MockLogService();
        resource.setLogservice(logservice);

        // Inject fake OSGi service UkelonnService
        UkelonnService ukelonn = mock(UkelonnService.class);
        resource.ukelonn = ukelonn;

        // Clear the Subject to ensure that Shiro will fail
        // no matter what order test methods are run in
        ThreadContext.remove();

        // Run the method under test
        resource.doRegisterJob(job);
    }

    @Test
    public void testUpdateJob() {
        UkelonnService ukelonn = mock(UkelonnService.class);
        JobResource resource = new JobResource();

        // Create mock OSGi services to inject and inject it
        MockLogService logservice = new MockLogService();
        resource.setLogservice(logservice);
        resource.ukelonn = ukelonn;

        Account account = getJadAccount();
        Transaction job = getJadJobs().get(0);
        int jobId = job.getId();

        // Save initial values of the job for comparison later
        Integer originalTransactionTypeId = job.getTransactionType().getId();
        Date originalTransactionTime = job.getTransactionTime();
        double originalTransactionAmount = job.getTransactionAmount();

        // Find a different job type that has a different amount
        TransactionType newJobType = findJobTypeWithDifferentIdAndAmount(ukelonn, originalTransactionTypeId, originalTransactionAmount);

        // Create a new job object with a different jobtype and the same id
        Date now = new Date();
        UpdatedTransaction editedJob = UpdatedTransaction.with()
            .id(jobId)
            .accountId(account.getAccountId())
            .transactionTypeId(newJobType.getId())
            .transactionTime(now)
            .transactionAmount(newJobType.getTransactionAmount())
            .build();
        when(ukelonn.updateJob(any())).thenReturn(Arrays.asList(convertUpdatedTransaction(editedJob)));

        List<Transaction> updatedJobs = resource.doUpdateJob(editedJob);

        Transaction editedJobFromDatabase = updatedJobs.stream().filter(t->t.getId() == job.getId()).collect(Collectors.toList()).get(0);

        assertEquals(editedJob.getTransactionTypeId(), editedJobFromDatabase.getTransactionType().getId().intValue());
        assertThat(editedJobFromDatabase.getTransactionTime().getTime()).isGreaterThan(originalTransactionTime.getTime());
        assertEquals(editedJob.getTransactionAmount(), editedJobFromDatabase.getTransactionAmount(), 0.0);
    }

    @Test(expected=InternalServerErrorException.class)
    public void testUpdateJobGetSQLException() throws Exception {
        // Create an ukelonn service with a mock database that throws exception
        UkelonnServiceProvider ukelonn = new UkelonnServiceProvider();
        DataSource datasource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        when(datasource.getConnection()).thenReturn(connection);
        PreparedStatement statement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        doThrow(SQLException.class).when(statement).setInt(anyInt(), anyInt());
        ukelonn.setDataSource(datasource);

        // Create a resource and inject OSGi services
        JobResource resource = new JobResource();
        resource.ukelonn = ukelonn;
        MockLogService logservice = new MockLogService();
        resource.setLogservice(logservice);

        resource.doUpdateJob(UpdatedTransaction.with().build());
        fail("Should never get here");
    }

    private TransactionType findJobTypeWithDifferentIdAndAmount(UkelonnService ukelonn, Integer transactionTypeId, double amount) {
        return getJobtypes().stream().filter(t->!t.getId().equals(transactionTypeId)).filter(t->t.getTransactionAmount() != amount).collect(Collectors.toList()).get(0);
    }

}
