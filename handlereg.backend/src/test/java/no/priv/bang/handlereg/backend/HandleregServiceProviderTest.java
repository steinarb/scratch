/*
 * Copyright 2019-2020 Steinar Bang
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
package no.priv.bang.handlereg.backend;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.ops4j.pax.jdbc.derby.impl.DerbyDataSourceFactory;
import org.osgi.service.jdbc.DataSourceFactory;

import no.priv.bang.handlereg.db.liquibase.test.HandleregTestDbLiquibaseRunner;
import no.priv.bang.handlereg.services.Butikk;
import no.priv.bang.handlereg.services.ButikkCount;
import no.priv.bang.handlereg.services.ButikkDate;
import no.priv.bang.handlereg.services.ButikkSum;
import no.priv.bang.handlereg.services.HandleregException;
import no.priv.bang.handlereg.services.NyHandling;
import no.priv.bang.handlereg.services.Oversikt;
import no.priv.bang.handlereg.services.SumYear;
import no.priv.bang.handlereg.services.SumYearMonth;
import no.priv.bang.handlereg.services.Transaction;
import no.priv.bang.osgi.service.mocks.logservice.MockLogService;
import no.priv.bang.osgiservice.users.User;
import no.priv.bang.osgiservice.users.UserManagementService;

class HandleregServiceProviderTest {
    private static DataSource datasource;

    @BeforeAll
    static void commonSetupForAllTests() throws Exception {
        DataSourceFactory derbyDataSourceFactory = new DerbyDataSourceFactory();
        Properties properties = new Properties();
        properties.setProperty(DataSourceFactory.JDBC_URL, "jdbc:derby:memory:handlereg;create=true");
        datasource = derbyDataSourceFactory.createDataSource(properties);
        MockLogService logservice = new MockLogService();
        HandleregTestDbLiquibaseRunner runner = new HandleregTestDbLiquibaseRunner();
        runner.setLogService(logservice);
        runner.activate();
        runner.prepare(datasource);
    }

    @Test
    void testHentOversikt() {
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        HandleregServiceProvider handlereg = new HandleregServiceProvider();
        when(useradmin.getUser(anyString())).thenReturn(new User(1, "jod", "jd@gmail.com", "John", "Doe"));
        handlereg.setLogservice(logservice);
        handlereg.setDatasource(datasource);
        handlereg.setUseradmin(useradmin);
        handlereg.activate();

        Oversikt jod = handlereg.finnOversikt("jod");
        assertEquals(1, jod.getAccountid());
        assertEquals("jod", jod.getBrukernavn());
        assertEquals("John", jod.getFornavn());
        assertEquals("Doe", jod.getEtternavn());
        assertThat(jod.getBalanse()).isGreaterThan(0.0);
    }

    @Test
    void testHentOversiktMedDbFeil() throws Exception {
        MockLogService logservice = new MockLogService();
        DataSource mockdb = createMockDbWithResultSetThatThrowsExceptionWhenIterated();
        UserManagementService useradmin = mock(UserManagementService.class);
        HandleregServiceProvider handlereg = new HandleregServiceProvider();
        handlereg.setLogservice(logservice);
        handlereg.setDatasource(mockdb);
        handlereg.setUseradmin(useradmin);
        handlereg.activate();

        assertEquals(0, logservice.getLogmessages().size());
        assertThrows(HandleregException.class, () -> {
                handlereg.finnOversikt("jd");
            });
        assertEquals(1, logservice.getLogmessages().size());
    }

    @Test
    void testHentOversiktMedTomtResultat() throws Exception {
        MockLogService logservice = new MockLogService();
        DataSource mockdb = createMockDbWithEmptyResultset();
        UserManagementService useradmin = mock(UserManagementService.class);
        HandleregServiceProvider handlereg = new HandleregServiceProvider();
        handlereg.setLogservice(logservice);
        handlereg.setDatasource(mockdb);
        handlereg.setUseradmin(useradmin);
        handlereg.activate();

        assertEquals(0, logservice.getLogmessages().size());
        Oversikt jd = handlereg.finnOversikt("jd");
        assertNull(jd);
        assertEquals(0, logservice.getLogmessages().size());
    }

    @Test
    void testHentHandlinger() {
        MockLogService logservice = new MockLogService();
        HandleregServiceProvider handlereg = new HandleregServiceProvider();
        UserManagementService useradmin = mock(UserManagementService.class);
        when(useradmin.getUser(eq("jod"))).thenReturn(new User(1, "jod", "jod@gmail.com", "John", "Doe"));
        when(useradmin.getUser(eq("jad"))).thenReturn(new User(2, "jad", "jad@gmail.com", "Jane", "Doe"));
        handlereg.setLogservice(logservice);
        handlereg.setDatasource(datasource);
        handlereg.setUseradmin(useradmin);
        handlereg.activate();

        Oversikt jod = handlereg.finnOversikt("jod");
        List<Transaction> handlingerJod = handlereg.findLastTransactions(jod.getAccountid());
        assertEquals(5, handlingerJod.size());

        Oversikt jad = handlereg.finnOversikt("jad");
        List<Transaction> handlingerJad = handlereg.findLastTransactions(jad.getAccountid());
        assertEquals(5, handlingerJad.size());
    }

    @Test
    void testHentHandlingerMedDbFeil() throws Exception {
        MockLogService logservice = new MockLogService();
        DataSource mockdb = createMockDbWithResultSetThatThrowsExceptionWhenIterated();
        UserManagementService useradmin = mock(UserManagementService.class);
        HandleregServiceProvider handlereg = new HandleregServiceProvider();
        handlereg.setLogservice(logservice);
        handlereg.setDatasource(mockdb);
        handlereg.setUseradmin(useradmin);
        handlereg.activate();

        assertEquals(0, logservice.getLogmessages().size());
        assertThrows(HandleregException.class, () -> {
                handlereg.findLastTransactions(1);
            });
        assertEquals(1, logservice.getLogmessages().size());
    }

    @Test
    void testRegistrerHandling() {
        MockLogService logservice = new MockLogService();
        HandleregServiceProvider handlereg = new HandleregServiceProvider();
        UserManagementService useradmin = mock(UserManagementService.class);
        when(useradmin.getUser(anyString())).thenReturn(new User(1, "jod", "jd@gmail.com", "John", "Doe"));
        handlereg.setLogservice(logservice);
        handlereg.setDatasource(datasource);
        handlereg.setUseradmin(useradmin);
        handlereg.activate();

        Oversikt originalOversikt = handlereg.finnOversikt("jod");
        double originalBalanse = originalOversikt.getBalanse();
        double nyttBelop = 510;
        Date now = new Date();
        NyHandling nyHandling = new NyHandling("jod", 1, 1, nyttBelop, now);
        Oversikt nyOversikt = handlereg.registrerHandling(nyHandling);
        assertThat(nyOversikt.getBalanse()).isEqualTo(originalBalanse + nyttBelop);
    }

    @Test
    void testRegistrerHandlingNoDate() {
        MockLogService logservice = new MockLogService();
        HandleregServiceProvider handlereg = new HandleregServiceProvider();
        UserManagementService useradmin = mock(UserManagementService.class);
        when(useradmin.getUser(anyString())).thenReturn(new User(1, "jod", "jd@gmail.com", "John", "Doe"));
        handlereg.setLogservice(logservice);
        handlereg.setDatasource(datasource);
        handlereg.setUseradmin(useradmin);
        handlereg.activate();

        Oversikt originalOversikt = handlereg.finnOversikt("jod");
        double originalBalanse = originalOversikt.getBalanse();
        double nyttBelop = 510;
        NyHandling nyHandling = new NyHandling("jod", 1, 1, nyttBelop, null);
        Oversikt nyOversikt = handlereg.registrerHandling(nyHandling);
        assertThat(nyOversikt.getBalanse()).isEqualTo(originalBalanse + nyttBelop);
    }

    @Test
    void testRegistrerHandlingMedDbFeil() throws Exception {
        MockLogService logservice = new MockLogService();
        DataSource mockdb = createMockDbThrowingException();
        UserManagementService useradmin = mock(UserManagementService.class);
        HandleregServiceProvider handlereg = new HandleregServiceProvider();
        handlereg.setLogservice(logservice);
        handlereg.setDatasource(mockdb);
        handlereg.setUseradmin(useradmin);
        handlereg.activate();

        double nyttBelop = 510;
        Date now = new Date();
        NyHandling nyHandling = new NyHandling("jd", 1, 1, nyttBelop, now);
        assertThrows(HandleregException.class, () -> {
                handlereg.registrerHandling(nyHandling);
            });
    }

    @Test
    void testFinnButikker() {
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        HandleregServiceProvider handlereg = new HandleregServiceProvider();
        handlereg.setLogservice(logservice);
        handlereg.setDatasource(datasource);
        handlereg.setUseradmin(useradmin);
        handlereg.activate();

        List<Butikk> butikker = handlereg.finnButikker();
        assertEquals(133, butikker.size());
    }

    @Test
    void testFinnButikkerMedDbFeil() throws Exception {
        MockLogService logservice = new MockLogService();
        DataSource mockdb = createMockDbWithResultSetThatThrowsExceptionWhenIterated();
        UserManagementService useradmin = mock(UserManagementService.class);
        HandleregServiceProvider handlereg = new HandleregServiceProvider();
        handlereg.setLogservice(logservice);
        handlereg.setDatasource(mockdb);
        handlereg.setUseradmin(useradmin);
        handlereg.activate();

        assertEquals(0, logservice.getLogmessages().size());
        assertThrows(HandleregException.class, () -> {
                handlereg.finnButikker();
            });
        assertEquals(1, logservice.getLogmessages().size());
    }

    @Test
    void testLeggTilButikk() {
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        HandleregServiceProvider handlereg = new HandleregServiceProvider();
        handlereg.setLogservice(logservice);
        handlereg.setDatasource(datasource);
        handlereg.setUseradmin(useradmin);
        handlereg.activate();

        List<Butikk> butikkerFoerOppdatering = handlereg.finnButikker();
        Butikk nybutikk = new Butikk("Spar fjellheimen");
        List<Butikk> butikker = handlereg.leggTilButikk(nybutikk);
        assertEquals(butikkerFoerOppdatering.size() + 1, butikker.size());
    }

    @Test
    void testLeggTilButikkMedDbFeilVedLagring() throws Exception {
        MockLogService logservice = new MockLogService();
        DataSource mockdb = createMockDbThrowingException();
        UserManagementService useradmin = mock(UserManagementService.class);
        HandleregServiceProvider handlereg = new HandleregServiceProvider();
        handlereg.setLogservice(logservice);
        handlereg.setDatasource(mockdb);
        handlereg.setUseradmin(useradmin);
        handlereg.activate();

        Butikk nybutikk = new Butikk("Spar fjellheimen", 2, 1500);
        assertThrows(HandleregException.class, () -> {
                handlereg.leggTilButikk(nybutikk);
            });
    }

    @Test
    void testEndreButikk() {
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        HandleregServiceProvider handlereg = new HandleregServiceProvider();
        handlereg.setLogservice(logservice);
        handlereg.setDatasource(datasource);
        handlereg.setUseradmin(useradmin);
        handlereg.activate();

        List<Butikk> butikkerFoerEndring = handlereg.finnButikker();
        Butikk butikk = butikkerFoerEndring.get(10);
        int butikkId = butikk.getStoreId();
        String nyttButikkNavn = "Joker Særbøåsen";
        Butikk butikkMedEndretTittel = endreTittel(butikk, nyttButikkNavn);
        List<Butikk> butikker = handlereg.endreButikk(butikkMedEndretTittel);
        Butikk oppdatertButikk = butikker.stream().filter(b -> b.getStoreId() == butikkId).findFirst().get();
        assertEquals(nyttButikkNavn, oppdatertButikk.getButikknavn());
    }

    @Test
    void testEndreButikkMedIdSomIkkeFinnes() {
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        HandleregServiceProvider handlereg = new HandleregServiceProvider();
        handlereg.setLogservice(logservice);
        handlereg.setDatasource(datasource);
        handlereg.setUseradmin(useradmin);
        handlereg.activate();

        List<Butikk> butikkerFoerEndring = handlereg.finnButikker();
        int idPaaButikkSomIkkeFinnes = 500;
        Butikk butikkMedEndretTittel = new Butikk(idPaaButikkSomIkkeFinnes, "Tullebutikk", 300, 400);
        List<Butikk> butikker = handlereg.endreButikk(butikkMedEndretTittel);
        assertEquals(butikkerFoerEndring.size(), butikker.size());
        assertEquals(0, logservice.getLogmessages().size()); // Blir tydeligvis ikke noen SQLExceptin av update på en rad som ikke finnes?
        Optional<Butikk> oppdatertButikk = butikker.stream().filter(b -> b.getStoreId() == idPaaButikkSomIkkeFinnes).findFirst();
        assertFalse(oppdatertButikk.isPresent()); // Men butikken med ikke-eksisterende id blir heller ikke inserted
    }

    @Test
    void testFinnNesteLedigeRekkefolgeForGruppe() throws Exception {
        MockLogService logservice = new MockLogService();
        HandleregServiceProvider handlereg = new HandleregServiceProvider();
        UserManagementService useradmin = mock(UserManagementService.class);
        handlereg.setLogservice(logservice);
        handlereg.setDatasource(datasource);
        handlereg.setUseradmin(useradmin);
        handlereg.activate();

        int sisteLedigeForGruppe1 = finnSisteRekkefolgeForgruppe(1);
        int nesteLedigeRekkefolgeForGruppe1 = handlereg.finnNesteLedigeRekkefolgeForGruppe(1);
        assertEquals(sisteLedigeForGruppe1 + 10, nesteLedigeRekkefolgeForGruppe1);
        int sisteLedigeForGruppe2 = finnSisteRekkefolgeForgruppe(2);
        int nesteLedigeRekkefolgeForGruppe2 = handlereg.finnNesteLedigeRekkefolgeForGruppe(2);
        assertEquals(sisteLedigeForGruppe2 + 10, nesteLedigeRekkefolgeForGruppe2);
    }

    @Test
    void testFinnNesteLedigeRekkefolgeNaarDetIkkeErNoenTreff() throws Exception {
        MockLogService logservice = new MockLogService();
        DataSource mockdb = createMockDbWithEmptyResultset();
        UserManagementService useradmin = mock(UserManagementService.class);
        HandleregServiceProvider handlereg = new HandleregServiceProvider();
        handlereg.setLogservice(logservice);
        handlereg.setDatasource(mockdb);
        handlereg.setUseradmin(useradmin);
        handlereg.activate();

        int nesteLedigeRekkefolge = handlereg.finnNesteLedigeRekkefolgeForGruppe(1);
        assertEquals(0, nesteLedigeRekkefolge);
    }

    @Test
    void testFinnNesteLedigeRekkefolgeNaarDetBlirKastetException() throws Exception {
        MockLogService logservice = new MockLogService();
        DataSource mockdb = createMockDbThrowingException();
        UserManagementService useradmin = mock(UserManagementService.class);
        HandleregServiceProvider handlereg = new HandleregServiceProvider();
        handlereg.setLogservice(logservice);
        handlereg.setDatasource(mockdb);
        handlereg.setUseradmin(useradmin);
        handlereg.activate();

        assertThrows(HandleregException.class, () -> {
                handlereg.finnNesteLedigeRekkefolgeForGruppe(1);
            });
    }

    @Test
    void testSumOverButikk() {
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        HandleregServiceProvider handlereg = new HandleregServiceProvider();
        handlereg.setLogservice(logservice);
        handlereg.setDatasource(datasource);
        handlereg.setUseradmin(useradmin);
        handlereg.activate();

        List<ButikkSum> sumOverButikk = handlereg.sumOverButikk();
        assertThat(sumOverButikk.size()).isPositive();
    }

    @Test
    void testAntallHandlerIButikk() {
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        HandleregServiceProvider handlereg = new HandleregServiceProvider();
        handlereg.setLogservice(logservice);
        handlereg.setDatasource(datasource);
        handlereg.setUseradmin(useradmin);
        handlereg.activate();

        List<ButikkCount> antallHandlerIButikk = handlereg.antallHandlingerIButikk();
        assertThat(antallHandlerIButikk.size()).isPositive();
    }

    @Test
    void testSisteHandelIButikk() {
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        HandleregServiceProvider handlereg = new HandleregServiceProvider();
        handlereg.setLogservice(logservice);
        handlereg.setDatasource(datasource);
        handlereg.setUseradmin(useradmin);
        handlereg.activate();

        List<ButikkDate> sisteHandelIButikk = handlereg.sisteHandelIButikk();
        assertThat(sisteHandelIButikk.size()).isPositive();
    }

    @Test
    void testTotaltHandlebelopPrAar() {
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        HandleregServiceProvider handlereg = new HandleregServiceProvider();
        handlereg.setLogservice(logservice);
        handlereg.setDatasource(datasource);
        handlereg.setUseradmin(useradmin);
        handlereg.activate();

        List<SumYear> totaltHandlebelopPrAar = handlereg.totaltHandlebelopPrAar();
        assertThat(totaltHandlebelopPrAar.size()).isPositive();
    }

    @Test
    void testTotaltHandlebelopPrAarOgMaaned() {
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        HandleregServiceProvider handlereg = new HandleregServiceProvider();
        handlereg.setLogservice(logservice);
        handlereg.setDatasource(datasource);
        handlereg.setUseradmin(useradmin);
        handlereg.activate();

        List<SumYearMonth> totaltHandlebelopPrAarOgMaaned = handlereg.totaltHandlebelopPrAarOgMaaned();
        assertThat(totaltHandlebelopPrAarOgMaaned.size()).isPositive();
    }

    private DataSource createMockDbWithEmptyResultset() throws SQLException {
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet results = mock(ResultSet.class);
        when(results.next()).thenReturn(false);
        when(statement.executeQuery()).thenReturn(results);
        DataSource mockdb = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(mockdb.getConnection()).thenReturn(connection);
        return mockdb;
    }

    @SuppressWarnings("unchecked")
    private DataSource createMockDbWithResultSetThatThrowsExceptionWhenIterated() throws SQLException {
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet results = mock(ResultSet.class);
        when(results.next()).thenThrow(SQLException.class);
        when(statement.executeQuery()).thenReturn(results);
        DataSource mockdb = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(mockdb.getConnection()).thenReturn(connection);
        return mockdb;
    }

    @SuppressWarnings("unchecked")
    private DataSource createMockDbThrowingException() throws SQLException {
        DataSource mockdb = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        when(connection.prepareStatement(anyString())).thenThrow(SQLException.class);
        when(mockdb.getConnection()).thenReturn(connection);
        return mockdb;
    }

    private int finnSisteRekkefolgeForgruppe(int gruppe) throws Exception {
        try (Connection connection = datasource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("select rekkefolge from stores where gruppe=? order by rekkefolge desc fetch next 1 rows only")) {
                statement.setInt(1, gruppe);
                try (ResultSet results = statement.executeQuery()) {
                    if (results.next()) {
                        return results.getInt(1);
                    }
                }
            }
        }

        return -1;
    }

    private Butikk endreTittel(Butikk butikk, String butikknavn) {
        int id = butikk.getStoreId();
        int gruppe = butikk.getRekkefolge();
        int rekkefolge = butikk.getRekkefolge();
        return new Butikk(id, butikknavn, gruppe, rekkefolge);
    }

}
