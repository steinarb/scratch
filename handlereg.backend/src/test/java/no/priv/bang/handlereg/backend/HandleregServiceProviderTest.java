/*
 * Copyright 2019-2024 Steinar Bang
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
import java.util.Optional;
import java.util.Properties;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.ops4j.pax.jdbc.derby.impl.DerbyDataSourceFactory;
import org.osgi.service.jdbc.DataSourceFactory;

import no.priv.bang.handlereg.db.liquibase.test.HandleregTestDbLiquibaseRunner;
import no.priv.bang.handlereg.services.Butikk;
import no.priv.bang.handlereg.services.Favorittpar;
import no.priv.bang.handlereg.services.NyFavoritt;
import no.priv.bang.handlereg.services.HandleregException;
import no.priv.bang.handlereg.services.NyHandling;
import no.priv.bang.osgi.service.mocks.logservice.MockLogService;
import no.priv.bang.osgiservice.users.User;
import no.priv.bang.osgiservice.users.UserManagementService;

class HandleregServiceProviderTest {
    private static DataSource datasource;

    @BeforeAll
    static void commonSetupForAllTests() throws Exception {
        var derbyDataSourceFactory = new DerbyDataSourceFactory();
        var properties = new Properties();
        properties.setProperty(DataSourceFactory.JDBC_URL, "jdbc:derby:memory:handlereg;create=true");
        datasource = derbyDataSourceFactory.createDataSource(properties);
        var logservice = new MockLogService();
        var runner = new HandleregTestDbLiquibaseRunner();
        runner.setLogService(logservice);
        runner.activate();
        runner.prepare(datasource);
    }

    @Test
    void testHentOversikt() {
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var handlereg = new HandleregServiceProvider();
        when(useradmin.getUser(anyString())).thenReturn(User.with().userid(1).username("jod").email("jd@gmail.com").firstname("John").lastname("Doe").build());
        handlereg.setLogservice(logservice);
        handlereg.setDatasource(datasource);
        handlereg.setUseradmin(useradmin);
        handlereg.activate();

        var jod = handlereg.finnOversikt("jod");
        assertEquals(1, jod.accountid());
        assertEquals("jod", jod.brukernavn());
        assertEquals("John", jod.fornavn());
        assertEquals("Doe", jod.etternavn());
        assertThat(jod.balanse()).isGreaterThan(0.0);
        assertThat(jod.sumPreviousMonth()).isGreaterThan(0.0);
        assertThat(jod.sumThisMonth()).isGreaterThan(0.0);
        assertThat(jod.lastTransactionAmount()).isGreaterThan(0.0);
    }

    @Test
    void testHentOversiktMedDbFeil() throws Exception {
        var logservice = new MockLogService();
        var mockdb = createMockDbWithResultSetThatThrowsExceptionWhenIterated();
        var useradmin = mock(UserManagementService.class);
        var handlereg = new HandleregServiceProvider();
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
        var logservice = new MockLogService();
        var mockdb = createMockDbWithEmptyResultset();
        var useradmin = mock(UserManagementService.class);
        var handlereg = new HandleregServiceProvider();
        handlereg.setLogservice(logservice);
        handlereg.setDatasource(mockdb);
        handlereg.setUseradmin(useradmin);
        handlereg.activate();

        assertEquals(0, logservice.getLogmessages().size());
        var jd = handlereg.finnOversikt("jd");
        assertNull(jd);
        assertEquals(0, logservice.getLogmessages().size());
    }

    @Test
    void testHentHandlinger() {
        var logservice = new MockLogService();
        var handlereg = new HandleregServiceProvider();
        var useradmin = mock(UserManagementService.class);
        when(useradmin.getUser("jod")).thenReturn(User.with().userid(1).username("jod").email("jod@gmail.com").firstname("John").lastname("Doe").build());
        when(useradmin.getUser("jad")).thenReturn(User.with().userid(2).username("jad").email("jad@gmail.com").firstname("Jane").lastname("Doe").build());
        handlereg.setLogservice(logservice);
        handlereg.setDatasource(datasource);
        handlereg.setUseradmin(useradmin);
        handlereg.activate();

        var jod = handlereg.finnOversikt("jod");
        var handlingerJod = handlereg.findLastTransactions(jod.accountid());
        assertEquals(5, handlingerJod.size());

        var jad = handlereg.finnOversikt("jad");
        var handlingerJad = handlereg.findLastTransactions(jad.accountid());
        assertEquals(5, handlingerJad.size());
    }

    @Test
    void testHentHandlingerMedDbFeil() throws Exception {
        var logservice = new MockLogService();
        var mockdb = createMockDbWithResultSetThatThrowsExceptionWhenIterated();
        var useradmin = mock(UserManagementService.class);
        var handlereg = new HandleregServiceProvider();
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
        var logservice = new MockLogService();
        var handlereg = new HandleregServiceProvider();
        var useradmin = mock(UserManagementService.class);
        when(useradmin.getUser(anyString())).thenReturn(User.with().userid(1).username("jod").email("jd@gmail.com").firstname("John").lastname("Doe").build());
        handlereg.setLogservice(logservice);
        handlereg.setDatasource(datasource);
        handlereg.setUseradmin(useradmin);
        handlereg.activate();

        var originalOversikt = handlereg.finnOversikt("jod");
        var originalBalanse = originalOversikt.balanse();
        var nyttBelop = 510;
        var now = new Date();
        var nyHandling = NyHandling.with()
            .username("jod")
            .accountid(1)
            .storeId(1)
            .belop(nyttBelop)
            .handletidspunkt(now)
            .build();
        var nyOversikt = handlereg.registrerHandling(nyHandling);
        assertThat(nyOversikt.balanse()).isEqualTo(originalBalanse + nyttBelop);
        assertThat(nyOversikt.lastTransactionStore()).isEqualTo(1);
    }

    @Test
    void testRegistrerHandlingNoDate() {
        var logservice = new MockLogService();
        var handlereg = new HandleregServiceProvider();
        var useradmin = mock(UserManagementService.class);
        when(useradmin.getUser(anyString())).thenReturn(User.with().userid(1).username("jod").email("jd@gmail.com").firstname("John").lastname("Doe").build());
        handlereg.setLogservice(logservice);
        handlereg.setDatasource(datasource);
        handlereg.setUseradmin(useradmin);
        handlereg.activate();

        var originalOversikt = handlereg.finnOversikt("jod");
        var originalBalanse = originalOversikt.balanse();
        var nyttBelop = 510;
        var nyHandling = NyHandling.with()
            .username("jod")
            .accountid(1)
            .storeId(1)
            .belop(nyttBelop)
            .build();
        var nyOversikt = handlereg.registrerHandling(nyHandling);
        assertThat(nyOversikt.balanse()).isEqualTo(originalBalanse + nyttBelop);
    }

    @Test
    void testRegistrerHandlingMedDbFeil() throws Exception {
        var logservice = new MockLogService();
        var mockdb = createMockDbThrowingException();
        var useradmin = mock(UserManagementService.class);
        var handlereg = new HandleregServiceProvider();
        handlereg.setLogservice(logservice);
        handlereg.setDatasource(mockdb);
        handlereg.setUseradmin(useradmin);
        handlereg.activate();

        var nyttBelop = 510;
        var now = new Date();
        var nyHandling = NyHandling.with()
            .username("jd")
            .accountid(1)
            .storeId(1)
            .belop(nyttBelop)
            .handletidspunkt(now)
            .build();
        assertThrows(HandleregException.class, () -> {
                handlereg.registrerHandling(nyHandling);
            });
    }

    @Test
    void testFinnButikker() {
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var handlereg = new HandleregServiceProvider();
        handlereg.setLogservice(logservice);
        handlereg.setDatasource(datasource);
        handlereg.setUseradmin(useradmin);
        handlereg.activate();

        var butikker = handlereg.finnButikker();
        assertEquals(133, butikker.size());
    }

    @Test
    void testFinnButikkerMedDbFeil() throws Exception {
        var logservice = new MockLogService();
        var mockdb = createMockDbWithResultSetThatThrowsExceptionWhenIterated();
        var useradmin = mock(UserManagementService.class);
        var handlereg = new HandleregServiceProvider();
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
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var handlereg = new HandleregServiceProvider();
        handlereg.setLogservice(logservice);
        handlereg.setDatasource(datasource);
        handlereg.setUseradmin(useradmin);
        handlereg.activate();

        var butikkerFoerOppdatering = handlereg.finnButikker();
        var nybutikk = Butikk.with().butikknavn("Spar fjellheimen").build();
        var butikker = handlereg.leggTilButikk(nybutikk);
        assertEquals(butikkerFoerOppdatering.size() + 1, butikker.size());
    }

    @Test
    void testLeggTilButikkMedDbFeilVedLagring() throws Exception {
        var logservice = new MockLogService();
        var mockdb = createMockDbThrowingException();
        var useradmin = mock(UserManagementService.class);
        var handlereg = new HandleregServiceProvider();
        handlereg.setLogservice(logservice);
        handlereg.setDatasource(mockdb);
        handlereg.setUseradmin(useradmin);
        handlereg.activate();

        var nybutikk = Butikk.with().butikknavn("Spar fjellheimen").gruppe(2).rekkefolge(1500).build();
        assertThrows(HandleregException.class, () -> {
                handlereg.leggTilButikk(nybutikk);
            });
    }

    @Test
    void testEndreButikk() {
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var handlereg = new HandleregServiceProvider();
        handlereg.setLogservice(logservice);
        handlereg.setDatasource(datasource);
        handlereg.setUseradmin(useradmin);
        handlereg.activate();

        var butikkerFoerEndring = handlereg.finnButikker();
        var butikk = butikkerFoerEndring.get(10);
        var butikkId = butikk.storeId();
        var nyttButikkNavn = "Joker Særbøåsen";
        var butikkMedEndretTittel = endreTittel(butikk, nyttButikkNavn);
        var butikker = handlereg.endreButikk(butikkMedEndretTittel);
        var oppdatertButikk = butikker.stream().filter(b -> b.storeId() == butikkId).findFirst().get();
        assertEquals(nyttButikkNavn, oppdatertButikk.butikknavn());
    }

    @Test
    void testEndreButikkMedIdSomIkkeFinnes() {
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var handlereg = new HandleregServiceProvider();
        handlereg.setLogservice(logservice);
        handlereg.setDatasource(datasource);
        handlereg.setUseradmin(useradmin);
        handlereg.activate();

        var butikkerFoerEndring = handlereg.finnButikker();
        var idPaaButikkSomIkkeFinnes = 500;
        var butikkMedEndretTittel = Butikk.with().storeId(idPaaButikkSomIkkeFinnes).butikknavn("Tullebutikk").gruppe(300).rekkefolge(400).build();
        var butikker = handlereg.endreButikk(butikkMedEndretTittel);
        assertEquals(butikkerFoerEndring.size(), butikker.size());
        assertEquals(0, logservice.getLogmessages().size()); // Blir tydeligvis ikke noen SQLExceptin av update på en rad som ikke finnes?
        Optional<Butikk> oppdatertButikk = butikker.stream().filter(b -> b.storeId() == idPaaButikkSomIkkeFinnes).findFirst();
        assertFalse(oppdatertButikk.isPresent()); // Men butikken med ikke-eksisterende id blir heller ikke inserted
    }

    @Test
    void testFinnNesteLedigeRekkefolgeForGruppe() throws Exception {
        var logservice = new MockLogService();
        var handlereg = new HandleregServiceProvider();
        var useradmin = mock(UserManagementService.class);
        handlereg.setLogservice(logservice);
        handlereg.setDatasource(datasource);
        handlereg.setUseradmin(useradmin);
        handlereg.activate();

        var sisteLedigeForGruppe1 = finnSisteRekkefolgeForgruppe(1);
        var nesteLedigeRekkefolgeForGruppe1 = handlereg.finnNesteLedigeRekkefolgeForGruppe(1);
        assertEquals(sisteLedigeForGruppe1 + 10, nesteLedigeRekkefolgeForGruppe1);
        var sisteLedigeForGruppe2 = finnSisteRekkefolgeForgruppe(2);
        var nesteLedigeRekkefolgeForGruppe2 = handlereg.finnNesteLedigeRekkefolgeForGruppe(2);
        assertEquals(sisteLedigeForGruppe2 + 10, nesteLedigeRekkefolgeForGruppe2);
    }

    @Test
    void testFinnNesteLedigeRekkefolgeNaarDetIkkeErNoenTreff() throws Exception {
        var logservice = new MockLogService();
        var mockdb = createMockDbWithEmptyResultset();
        var useradmin = mock(UserManagementService.class);
        var handlereg = new HandleregServiceProvider();
        handlereg.setLogservice(logservice);
        handlereg.setDatasource(mockdb);
        handlereg.setUseradmin(useradmin);
        handlereg.activate();

        var nesteLedigeRekkefolge = handlereg.finnNesteLedigeRekkefolgeForGruppe(1);
        assertEquals(0, nesteLedigeRekkefolge);
    }

    @Test
    void testFinnNesteLedigeRekkefolgeNaarDetBlirKastetException() throws Exception {
        var logservice = new MockLogService();
        var mockdb = createMockDbThrowingException();
        var useradmin = mock(UserManagementService.class);
        var handlereg = new HandleregServiceProvider();
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
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var handlereg = new HandleregServiceProvider();
        handlereg.setLogservice(logservice);
        handlereg.setDatasource(datasource);
        handlereg.setUseradmin(useradmin);
        handlereg.activate();

        var sumOverButikk = handlereg.sumOverButikk();
        assertThat(sumOverButikk).isNotEmpty();
    }

    @Test
    void testAntallHandlerIButikk() {
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var handlereg = new HandleregServiceProvider();
        handlereg.setLogservice(logservice);
        handlereg.setDatasource(datasource);
        handlereg.setUseradmin(useradmin);
        handlereg.activate();

        var antallHandlerIButikk = handlereg.antallHandlingerIButikk();
        assertThat(antallHandlerIButikk).isNotEmpty();
    }

    @Test
    void testSisteHandelIButikk() {
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var handlereg = new HandleregServiceProvider();
        handlereg.setLogservice(logservice);
        handlereg.setDatasource(datasource);
        handlereg.setUseradmin(useradmin);
        handlereg.activate();

        var sisteHandelIButikk = handlereg.sisteHandelIButikk();
        assertThat(sisteHandelIButikk).isNotEmpty();
    }

    @Test
    void testTotaltHandlebelopPrAar() {
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var handlereg = new HandleregServiceProvider();
        handlereg.setLogservice(logservice);
        handlereg.setDatasource(datasource);
        handlereg.setUseradmin(useradmin);
        handlereg.activate();

        var totaltHandlebelopPrAar = handlereg.totaltHandlebelopPrAar();
        assertThat(totaltHandlebelopPrAar).isNotEmpty();
    }

    @Test
    void testTotaltHandlebelopPrAarOgMaaned() {
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var handlereg = new HandleregServiceProvider();
        handlereg.setLogservice(logservice);
        handlereg.setDatasource(datasource);
        handlereg.setUseradmin(useradmin);
        handlereg.activate();

        var totaltHandlebelopPrAarOgMaaned = handlereg.totaltHandlebelopPrAarOgMaaned();
        assertThat(totaltHandlebelopPrAarOgMaaned).isNotEmpty();
    }

    @Test
    void testFavoritter() {
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var handlereg = new HandleregServiceProvider();
        handlereg.setLogservice(logservice);
        handlereg.setDatasource(datasource);
        handlereg.setUseradmin(useradmin);
        handlereg.activate();

        var username = "jod";
        var favoritterOpprinnelig = handlereg.finnFavoritter(username);
        assertNotNull(favoritterOpprinnelig);

        // Opprett to favoritter
        var butikker = handlereg.finnButikker();
        var butikk1 = butikker.get(1);
        var favoritter1 = handlereg.leggTilFavoritt(NyFavoritt.with().brukernavn(username).butikk(butikk1).build());
        assertThat(favoritter1).hasSizeGreaterThan(favoritterOpprinnelig.size());
        var butikk2 = butikker.get(2);
        var favoritter2 = handlereg.leggTilFavoritt(NyFavoritt.with().brukernavn(username).butikk(butikk2).build());
        assertThat(favoritter2).hasSizeGreaterThan(favoritter1.size());
        var forsteFavorittIndeks = favoritter1.size() -1;
        var andreFavorittIndeks = favoritter1.size();
        var favoritt1 = favoritter2.get(forsteFavorittIndeks);
        var favoritt2 = favoritter2.get(andreFavorittIndeks);
        assertEquals(favoritt1.accountid(), favoritt2.accountid());
        assertEquals(butikk1, favoritt1.store());
        assertEquals(butikk2, favoritt2.store());
        assertThat(favoritt2.rekkefolge()).isGreaterThan(favoritt1.rekkefolge());

        // Bytt rekkefølge på de to favorittene
        var favoritterSomSkalFlippes = Favorittpar.with().forste(favoritt1).andre(favoritt2).build();
        var favoritter3 = handlereg.byttRekkefolge(favoritterSomSkalFlippes);
        assertEquals(favoritter2.size(), favoritter3.size());
        var flippetFavoritt1 = favoritter3.get(forsteFavorittIndeks);
        var flippetFavoritt2 = favoritter3.get(andreFavorittIndeks);
        assertEquals(flippetFavoritt1.favouriteid(), favoritt2.favouriteid());
        assertEquals(flippetFavoritt2.favouriteid(), favoritt1.favouriteid());
        assertThat(flippetFavoritt2.rekkefolge()).isGreaterThan(flippetFavoritt1.rekkefolge());

        // Slett en favoritt
        assertThat(favoritter3)
            .contains(flippetFavoritt1)
            .contains(flippetFavoritt2);
        var favoritter4 = handlereg.slettFavoritt(flippetFavoritt1);
        assertThat(favoritter4)
            .hasSizeLessThan(favoritter3.size())
            .doesNotContain(flippetFavoritt1)
            .contains(flippetFavoritt2);
    }

    @Test
    void testFinnFavoritterMedFeil() throws Exception {
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var handlereg = new HandleregServiceProvider();
        handlereg.setLogservice(logservice);
        var datasourceThrowingException = mock(DataSource.class);
        when(datasourceThrowingException.getConnection()).thenThrow(SQLException.class);
        handlereg.setDatasource(datasourceThrowingException);
        handlereg.setUseradmin(useradmin);
        handlereg.activate();

        var exception = assertThrows(HandleregException.class, () -> handlereg.finnFavoritter("jod"));
        assertThat(exception.getMessage()).startsWith("Failed to retrieve a list of favourites");
        assertThat(logservice.getLogmessages()).isNotEmpty();
    }

    @Test
    void testLeggTilFavorittMedFeil() throws Exception {
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var handlereg = new HandleregServiceProvider();
        handlereg.setLogservice(logservice);
        var datasourceThrowingException = mock(DataSource.class);
        when(datasourceThrowingException.getConnection()).thenThrow(SQLException.class);
        handlereg.setDatasource(datasourceThrowingException);
        handlereg.setUseradmin(useradmin);
        handlereg.activate();

        var exception = assertThrows(HandleregException.class, () -> handlereg.leggTilFavoritt(null));
        assertThat(exception.getMessage()).startsWith("Failed to insert a new favourite");
        assertThat(logservice.getLogmessages()).isNotEmpty();
    }

    @Test
    void testSlettFavorittMedFeil() throws Exception {
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var handlereg = new HandleregServiceProvider();
        handlereg.setLogservice(logservice);
        var datasourceThrowingException = mock(DataSource.class);
        when(datasourceThrowingException.getConnection()).thenThrow(SQLException.class);
        handlereg.setDatasource(datasourceThrowingException);
        handlereg.setUseradmin(useradmin);
        handlereg.activate();

        var exception = assertThrows(HandleregException.class, () -> handlereg.slettFavoritt(null));
        assertThat(exception.getMessage()).startsWith("Failed to delete favourite");
        assertThat(logservice.getLogmessages()).isNotEmpty();
    }

    @Test
    void testbyttRekkefolgeMedFeil() throws Exception {
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var handlereg = new HandleregServiceProvider();
        handlereg.setLogservice(logservice);
        var datasourceThrowingException = mock(DataSource.class);
        when(datasourceThrowingException.getConnection()).thenThrow(SQLException.class);
        handlereg.setDatasource(datasourceThrowingException);
        handlereg.setUseradmin(useradmin);
        handlereg.activate();

        var exception = assertThrows(HandleregException.class, () -> handlereg.byttRekkefolge(null));
        assertThat(exception.getMessage()).startsWith("Failed to swap order of favourite");
        assertThat(logservice.getLogmessages()).isNotEmpty();
    }

    @Test
    void testFinnFavoritterMedAccountidMedFeil() throws Exception {
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var handlereg = new HandleregServiceProvider();
        handlereg.setLogservice(logservice);
        var datasourceThrowingException = mock(DataSource.class);
        when(datasourceThrowingException.getConnection()).thenThrow(SQLException.class);
        handlereg.setDatasource(datasourceThrowingException);
        handlereg.setUseradmin(useradmin);
        handlereg.activate();

        var exception = assertThrows(HandleregException.class, () -> handlereg.finnFavoritterMedAccountid(1));
        assertThat(exception.getMessage()).startsWith("Failed to retrieve a list of favourites");
        assertThat(logservice.getLogmessages()).isNotEmpty();
    }

    @Test
    void testFinnSisteRekkefolgeIBrukersFavoritterMedAccountidMedFeil() throws Exception {
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var handlereg = new HandleregServiceProvider();
        handlereg.setLogservice(logservice);
        var datasourceThrowingException = mock(DataSource.class);
        when(datasourceThrowingException.getConnection()).thenThrow(SQLException.class);
        handlereg.setDatasource(datasourceThrowingException);
        handlereg.setUseradmin(useradmin);
        handlereg.activate();

        var connection = mock(Connection.class);
        when(connection.prepareStatement(anyString())).thenThrow(SQLException.class);
        var rekkefolge = handlereg.finnSisteRekkefolgeIBrukersFavoritter(connection, "jod");
        assertEquals(0, rekkefolge);
        assertThat(logservice.getLogmessages()).isNotEmpty();
    }

    private DataSource createMockDbWithEmptyResultset() throws SQLException {
        var statement = mock(PreparedStatement.class);
        var results = mock(ResultSet.class);
        when(results.next()).thenReturn(false);
        when(statement.executeQuery()).thenReturn(results);
        var mockdb = mock(DataSource.class);
        var connection = mock(Connection.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(connection.prepareStatement(anyString(), anyInt(), anyInt())).thenReturn(statement);
        when(mockdb.getConnection()).thenReturn(connection);
        return mockdb;
    }

    private DataSource createMockDbWithResultSetThatThrowsExceptionWhenIterated() throws SQLException {
        var statement = mock(PreparedStatement.class);
        var results = mock(ResultSet.class);
        when(results.next()).thenThrow(SQLException.class);
        when(statement.executeQuery()).thenReturn(results);
        var mockdb = mock(DataSource.class);
        var connection = mock(Connection.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(connection.prepareStatement(anyString(), anyInt(), anyInt())).thenReturn(statement);
        when(mockdb.getConnection()).thenReturn(connection);
        return mockdb;
    }

    private DataSource createMockDbThrowingException() throws SQLException {
        var mockdb = mock(DataSource.class);
        var connection = mock(Connection.class);
        when(connection.prepareStatement(anyString())).thenThrow(SQLException.class);
        when(mockdb.getConnection()).thenReturn(connection);
        return mockdb;
    }

    private int finnSisteRekkefolgeForgruppe(int gruppe) throws Exception {
        try (var connection = datasource.getConnection()) {
            try (var statement = connection.prepareStatement("select rekkefolge from stores where gruppe=? order by rekkefolge desc fetch next 1 rows only")) {
                statement.setInt(1, gruppe);
                try (var results = statement.executeQuery()) {
                    if (results.next()) {
                        return results.getInt(1);
                    }
                }
            }
        }

        return -1;
    }

    private Butikk endreTittel(Butikk butikk, String butikknavn) {
        return Butikk.with(butikk).butikknavn(butikknavn).build();
    }

}
