/*
 * Copyright 2023 Steinar Bang
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
package no.priv.bang.ratatoskr.backend;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import javax.sql.DataSource;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.ops4j.pax.jdbc.derby.impl.DerbyDataSourceFactory;
import org.osgi.service.jdbc.DataSourceFactory;

import no.priv.bang.ratatoskr.db.liquibase.test.RatatoskrTestDbLiquibaseRunner;
import no.priv.bang.ratatoskr.services.beans.Account;
import no.priv.bang.ratatoskr.services.beans.CounterBean;
import no.priv.bang.ratatoskr.services.beans.CounterIncrementStepBean;
import no.priv.bang.ratatoskr.services.beans.LocaleBean;
import no.priv.bang.osgi.service.mocks.logservice.MockLogService;
import no.priv.bang.osgiservice.users.Role;
import no.priv.bang.osgiservice.users.UserManagementService;
import static no.priv.bang.ratatoskr.services.RatatoskrConstants.*;

class RatatoskrServiceProviderTest {
    private final static Locale NB_NO = Locale.forLanguageTag("nb-no");

    private static DataSource datasource;

    @BeforeAll
    static void commonSetupForAllTests() throws Exception {
        DataSourceFactory derbyDataSourceFactory = new DerbyDataSourceFactory();
        Properties properties = new Properties();
        properties.setProperty(DataSourceFactory.JDBC_URL, "jdbc:derby:memory:ratatoskr;create=true");
        datasource = derbyDataSourceFactory.createDataSource(properties);
        RatatoskrTestDbLiquibaseRunner runner = new RatatoskrTestDbLiquibaseRunner();
        runner.activate();
        runner.prepare(datasource);
    }

    @Test
    void testGetAccounts() {
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        RatatoskrServiceProvider provider = new RatatoskrServiceProvider();
        provider.setLogservice(logservice);
        provider.setDatasource(datasource);
        provider.setUseradmin(useradmin);
        provider.activate(Collections.singletonMap("defaultlocale", "nb_NO"));

        List<Account> accountsBefore = provider.getAccounts();
        assertThat(accountsBefore).isEmpty();
        assertThat(provider.getCounterIncrementStep("jad")).isEmpty();
        assertThat(provider.getCounter("jad")).isEmpty();
        boolean accountCreated = provider.lazilyCreateAccount("jad");
        assertTrue(accountCreated);
        List<Account> accountsAfter = provider.getAccounts();
        assertThat(accountsAfter).isNotEmpty();
        int defaultInitialCounterIncrementStepValue = 1;
        Optional<CounterIncrementStepBean> counterIncrementStep = provider.getCounterIncrementStep("jad");
        assertThat(counterIncrementStep).isNotEmpty();
        assertEquals(defaultInitialCounterIncrementStepValue, counterIncrementStep.get().getCounterIncrementStep());
        int defaultInitialCounterValue = 0;
        Optional<CounterBean> counter = provider.getCounter("jad");
        assertThat(counter).isNotEmpty();
        assertEquals(defaultInitialCounterValue, counter.get().getCounter());
        boolean secondAccountCreate = provider.lazilyCreateAccount("jad");
        assertFalse(secondAccountCreate);
        List<Account> accountsAfterSecondCreate = provider.getAccounts();
        assertThat(accountsAfterSecondCreate).isEqualTo(accountsAfter);
    }

    @Test
    void testGetAccountsWithSQLException() throws Exception {
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        RatatoskrServiceProvider provider = new RatatoskrServiceProvider();
        DataSource datasourceThrowsException = mock(DataSource.class);
        when(datasourceThrowsException.getConnection()).thenThrow(SQLException.class);
        provider.setLogservice(logservice);
        provider.setDatasource(datasourceThrowsException);
        provider.setUseradmin(useradmin);
        provider.activate(Collections.singletonMap("defaultlocale", "nb_NO"));

        assertThat(logservice.getLogmessages()).isEmpty();
        List<Account> accounts = provider.getAccounts();
        assertThat(accounts).isEmpty();
        assertThat(logservice.getLogmessages()).isNotEmpty();
    }

    @Test
    void testLazilyCreateAccountWithSQLException() throws Exception {
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        RatatoskrServiceProvider provider = new RatatoskrServiceProvider();
        DataSource datasourceThrowsException = mock(DataSource.class);
        when(datasourceThrowsException.getConnection()).thenThrow(SQLException.class);
        provider.setLogservice(logservice);
        provider.setDatasource(datasourceThrowsException);
        provider.setUseradmin(useradmin);
        provider.activate(Collections.singletonMap("defaultlocale", "nb_NO"));

        assertThat(logservice.getLogmessages()).isEmpty();
        boolean accountCreated = provider.lazilyCreateAccount("jad");
        assertFalse(accountCreated);
        assertThat(logservice.getLogmessages()).isNotEmpty();
    }

    @Test
    void testThatRolesAreAddedIfMissing() {
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var provider = new RatatoskrServiceProvider();
        provider.setLogservice(logservice);
        provider.setDatasource(datasource);
        provider.setUseradmin(useradmin);
        provider.activate(Collections.singletonMap("defaultlocale", "nb_NO"));

        verify(useradmin, times(2)).addRole(any());
    }

    @Test
    void testThatRolesAreNotAddedIfPresent() {
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var existingroles = Arrays.asList(
            Role.with().rolename(RATATOSKRUSER_ROLE).build(),
            Role.with().rolename(RATATOSKRADMIN_ROLE).build());
        when(useradmin.getRoles()).thenReturn(existingroles);
        var provider = new RatatoskrServiceProvider();
        provider.setLogservice(logservice);
        provider.setDatasource(datasource);
        provider.setUseradmin(useradmin);
        provider.activate(Collections.singletonMap("defaultlocale", "nb_NO"));

        verify(useradmin, never()).addRole(any());
    }

    @Test
    void testThatSomeRolesAreNotAddedIfNotAllRolesArePresent() {
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var existingroles = Arrays.asList(
            Role.with().rolename(RATATOSKRADMIN_ROLE).build());
        when(useradmin.getRoles()).thenReturn(existingroles);
        var provider = new RatatoskrServiceProvider();
        provider.setLogservice(logservice);
        provider.setDatasource(datasource);
        provider.setUseradmin(useradmin);
        provider.activate(Collections.singletonMap("defaultlocale", "nb_NO"));

        verify(useradmin, times(1)).addRole(any());
    }

    @Test
    void testIncrementAndDecrement() {
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        RatatoskrServiceProvider provider = new RatatoskrServiceProvider();
        provider.setLogservice(logservice);
        provider.setDatasource(datasource);
        provider.setUseradmin(useradmin);
        provider.activate(Collections.singletonMap("defaultlocale", "nb_NO"));

        // Create new account with default values for counter and increment step
        provider.lazilyCreateAccount("on");
        CounterIncrementStepBean initialCounterIncrementStep = provider.getCounterIncrementStep("on").orElseThrow();
        CounterBean initialCounterValue = provider.getCounter("on").orElseThrow();

        // Set the increment step to the existing step value plus one
        CounterIncrementStepBean newIncrementStep = CounterIncrementStepBean.with()
            .username("on")
            .counterIncrementStep(initialCounterIncrementStep.getCounterIncrementStep() + 1)
            .build();
        CounterIncrementStepBean updatedIncrementStep = provider.updateCounterIncrementStep(newIncrementStep).orElseThrow();
        assertThat(updatedIncrementStep.getCounterIncrementStep()).isGreaterThan(initialCounterIncrementStep.getCounterIncrementStep());

        // Increment and verify the expected result
        int expectedIncrementedValue = initialCounterValue.getCounter() + updatedIncrementStep.getCounterIncrementStep();
        CounterBean incrementedValue = provider.incrementCounter("on").orElseThrow();
        assertEquals(expectedIncrementedValue, incrementedValue.getCounter());

        // Decrement and verify the expected result
        CounterBean decrementedValue = provider.decrementCounter("on").orElseThrow();
        assertEquals(initialCounterValue, decrementedValue);
    }

    @Test
    void testGetCounterIncrementStepWithSQLException() throws Exception {
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        RatatoskrServiceProvider provider = new RatatoskrServiceProvider();
        DataSource datasourceThrowsException = mock(DataSource.class);
        when(datasourceThrowsException.getConnection()).thenThrow(SQLException.class);
        provider.setLogservice(logservice);
        provider.setDatasource(datasourceThrowsException);
        provider.setUseradmin(useradmin);
        provider.activate(Collections.singletonMap("defaultlocale", "nb_NO"));

        assertThat(logservice.getLogmessages()).isEmpty();
        Optional<CounterIncrementStepBean> incrementStep = provider.getCounterIncrementStep("jad");
        assertThat(incrementStep).isEmpty();
        assertThat(logservice.getLogmessages()).isNotEmpty();
    }

    @Test
    void testUpdateCounterIncrementStepWithSQLException() throws Exception {
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        RatatoskrServiceProvider provider = new RatatoskrServiceProvider();
        DataSource datasourceThrowsException = mock(DataSource.class);
        when(datasourceThrowsException.getConnection()).thenThrow(SQLException.class);
        provider.setLogservice(logservice);
        provider.setDatasource(datasourceThrowsException);
        provider.setUseradmin(useradmin);
        provider.activate(Collections.singletonMap("defaultlocale", "nb_NO"));

        assertThat(logservice.getLogmessages()).isEmpty();
        Optional<CounterIncrementStepBean> updatedIncrementStep = provider.updateCounterIncrementStep(CounterIncrementStepBean.with().build());
        assertThat(updatedIncrementStep).isEmpty();
        assertThat(logservice.getLogmessages()).isNotEmpty();
    }

    @Test
    void testGetCounterWithSQLExceptio() throws Exception {
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        RatatoskrServiceProvider provider = new RatatoskrServiceProvider();
        DataSource datasourceThrowsException = mock(DataSource.class);
        when(datasourceThrowsException.getConnection()).thenThrow(SQLException.class);
        provider.setLogservice(logservice);
        provider.setDatasource(datasourceThrowsException);
        provider.setUseradmin(useradmin);
        provider.activate(Collections.singletonMap("defaultlocale", "nb_NO"));

        assertThat(logservice.getLogmessages()).isEmpty();
        Optional<CounterBean> counter = provider.getCounter("jad");
        assertThat(counter).isEmpty();
        assertThat(logservice.getLogmessages()).isNotEmpty();
    }

    @Test
    void testIncrementCounterWithSQLExceptio() throws Exception {
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        RatatoskrServiceProvider provider = new RatatoskrServiceProvider();
        DataSource datasourceThrowsException = mock(DataSource.class);
        when(datasourceThrowsException.getConnection()).thenThrow(SQLException.class);
        provider.setLogservice(logservice);
        provider.setDatasource(datasourceThrowsException);
        provider.setUseradmin(useradmin);
        provider.activate(Collections.singletonMap("defaultlocale", "nb_NO"));

        assertThat(logservice.getLogmessages()).isEmpty();
        Optional<CounterBean> incrementedCounter = provider.incrementCounter("jad");
        assertThat(incrementedCounter).isEmpty();
        assertThat(logservice.getLogmessages()).isNotEmpty();
    }

    @Test
    void testDecrementCounterWithSQLExceptio() throws Exception {
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        RatatoskrServiceProvider provider = new RatatoskrServiceProvider();
        DataSource datasourceThrowsException = mock(DataSource.class);
        when(datasourceThrowsException.getConnection()).thenThrow(SQLException.class);
        provider.setLogservice(logservice);
        provider.setDatasource(datasourceThrowsException);
        provider.setUseradmin(useradmin);
        provider.activate(Collections.singletonMap("defaultlocale", "nb_NO"));

        assertThat(logservice.getLogmessages()).isEmpty();
        Optional<CounterBean> decrementedCounter = provider.decrementCounter("jad");
        assertThat(decrementedCounter).isEmpty();
        assertThat(logservice.getLogmessages()).isNotEmpty();
    }

    @Test
    void testDefaultLocale() {
        RatatoskrServiceProvider ratatoskr = new RatatoskrServiceProvider();
        UserManagementService useradmin = mock(UserManagementService.class);
        ratatoskr.setUseradmin(useradmin);
        ratatoskr.activate(Collections.singletonMap("defaultlocale", "nb_NO"));
        assertEquals(NB_NO, ratatoskr.defaultLocale());
    }

    @Test
    void testAvailableLocales() {
        RatatoskrServiceProvider ratatoskr = new RatatoskrServiceProvider();
        UserManagementService useradmin = mock(UserManagementService.class);
        ratatoskr.setUseradmin(useradmin);
        ratatoskr.activate(Collections.singletonMap("defaultlocale", "nb_NO"));
        List<LocaleBean> locales = ratatoskr.availableLocales();
        assertThat(locales).isNotEmpty().contains(LocaleBean.with().locale(ratatoskr.defaultLocale()).build());
    }

    @Test
    void testDisplayTextsForDefaultLocale() {
        RatatoskrServiceProvider ratatoskr = new RatatoskrServiceProvider();
        UserManagementService useradmin = mock(UserManagementService.class);
        ratatoskr.setUseradmin(useradmin);
        ratatoskr.activate(Collections.singletonMap("defaultlocale", "nb_NO"));
        Map<String, String> displayTexts = ratatoskr.displayTexts(ratatoskr.defaultLocale());
        assertThat(displayTexts).isNotEmpty();
    }

    @Test
    void testDisplayText() {
        RatatoskrServiceProvider ratatoskr = new RatatoskrServiceProvider();
        UserManagementService useradmin = mock(UserManagementService.class);
        ratatoskr.setUseradmin(useradmin);
        ratatoskr.activate(Collections.singletonMap("defaultlocale", "nb_NO"));
        String text1 = ratatoskr.displayText("hi", "nb_NO");
        assertEquals("Hei", text1);
        String text2 = ratatoskr.displayText("hi", "en_GB");
        assertEquals("Hi", text2);
        String text3 = ratatoskr.displayText("hi", "");
        assertEquals("Hei", text3);
        String text4 = ratatoskr.displayText("hi", null);
        assertEquals("Hei", text4);
    }

}
