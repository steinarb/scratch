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
package no.priv.bang.handlereg.web.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.time.Month;
import java.time.Year;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.server.ServerProperties;
import org.junit.jupiter.api.Test;
import org.osgi.service.log.LogService;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mockrunner.mock.web.MockHttpServletRequest;
import com.mockrunner.mock.web.MockHttpServletResponse;
import com.mockrunner.mock.web.MockHttpSession;
import com.mockrunner.mock.web.MockServletOutputStream;

import no.priv.bang.handlereg.services.Butikk;
import no.priv.bang.handlereg.services.ButikkCount;
import no.priv.bang.handlereg.services.ButikkDate;
import no.priv.bang.handlereg.services.ButikkSum;
import no.priv.bang.handlereg.services.Credentials;
import no.priv.bang.handlereg.services.Favoritt;
import no.priv.bang.handlereg.services.Favorittpar;
import no.priv.bang.handlereg.services.HandleregService;
import no.priv.bang.handlereg.services.NyFavoritt;
import no.priv.bang.handlereg.services.NyHandling;
import no.priv.bang.handlereg.services.Oversikt;
import no.priv.bang.handlereg.services.SumYear;
import no.priv.bang.handlereg.services.SumYearMonth;
import no.priv.bang.handlereg.services.Transaction;
import no.priv.bang.osgi.service.mocks.logservice.MockLogService;

class HandleregWebApiTest extends ShiroTestBase {
    public static final ObjectMapper mapper = new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .findAndRegisterModules();

    @Test
    void testLogin() throws Exception {
        String username = "jd";
        String password = Base64.getEncoder().encodeToString("johnnyBoi".getBytes());
        Credentials credentials = Credentials.with().username(username).password(password).build();
        MockLogService logservice = new MockLogService();
        HandleregService handlereg = mock(HandleregService.class);
        HandleregWebApi servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(handlereg , logservice);
        createSubjectAndBindItToThread();
        MockHttpServletRequest request = buildPostUrl("/login");
        String postBody = mapper.writeValueAsString(credentials);
        request.setBodyContent(postBody);
        MockHttpServletResponse response = new MockHttpServletResponse();

        servlet.service(request, response);
        assertEquals(200, response.getStatus());

    }

    @Test
    void testLoginWrongPassword() throws Exception {
        String username = "jd";
        String password = Base64.getEncoder().encodeToString("johnnyBoi".getBytes());
        Credentials credentials = Credentials.with().username(username).password(password).build();
        MockLogService logservice = new MockLogService();
        HandleregService handlereg = mock(HandleregService.class);
        HandleregWebApi servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(handlereg , logservice);
        createSubjectAndBindItToThread();
        MockHttpServletRequest request = buildPostUrl("/login");
        String postBody = mapper.writeValueAsString(credentials);
        request.setBodyContent(postBody);
        MockHttpServletResponse response = new MockHttpServletResponse();

        servlet.service(request, response);
        assertEquals(200, response.getStatus());

    }

    @Test
    void testGetOversikt() throws Exception {
        HandleregService handlereg = mock(HandleregService.class);
        Oversikt jdOversikt = Oversikt.with()
            .accountid(1)
            .brukernavn("jd")
            .email("johndoe@gmail.com")
            .fornavn("John")
            .etternavn("Doe")
            .balanse(1500)
            .build();
        when(handlereg.finnOversikt("jd")).thenReturn(jdOversikt);
        MockLogService logservice = new MockLogService();
        HandleregWebApi servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(handlereg, logservice);
        MockHttpServletRequest request = buildGetUrl("/oversikt");
        MockHttpServletResponse response = new MockHttpServletResponse();

        loginUser(request, response, "jd", "johnnyBoi");
        servlet.service(request, response);
        assertEquals(200, response.getStatus());
    }

    @Test
    void testGetHandlinger() throws Exception {
        HandleregService handlereg = mock(HandleregService.class);
        when(handlereg.findLastTransactions(1)).thenReturn(Arrays.asList(Transaction.with().build()));
        MockLogService logservice = new MockLogService();
        HandleregWebApi servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(handlereg, logservice);
        MockHttpServletRequest request = buildGetUrl("/handlinger/1");
        MockHttpServletResponse response = new MockHttpServletResponse();

        loginUser(request, response, "jd", "johnnyBoi");
        servlet.service(request, response);
        assertEquals(200, response.getStatus());
    }

    @Test
    void testPostNyhandlinger() throws Exception {
        HandleregService handlereg = mock(HandleregService.class);
        Oversikt oversikt = Oversikt.with()
            .accountid(1)
            .brukernavn("jd")
            .email("johndoe@gmail.com")
            .fornavn("John")
            .etternavn("Doe")
            .balanse(500)
            .build();
        when(handlereg.registrerHandling(any())).thenReturn(oversikt);
        MockLogService logservice = new MockLogService();
        HandleregWebApi servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(handlereg, logservice);
        MockHttpServletRequest request = buildPostUrl("/nyhandling");
        NyHandling handling = NyHandling.with()
            .username("jd")
            .accountid(1)
            .storeId(1)
            .belop(510)
            .handletidspunkt(new Date())
            .build();
        String postBody = mapper.writeValueAsString(handling);
        request.setBodyContent(postBody);

        MockHttpServletResponse response = new MockHttpServletResponse();

        loginUser(request, response, "jd", "johnnyBoi");
        servlet.service(request, response);
        assertEquals(200, response.getStatus());
    }

    @Test
    void testGetButikker() throws Exception {
        HandleregService handlereg = mock(HandleregService.class);
        when(handlereg.finnButikker()).thenReturn(Arrays.asList(Butikk.with().build()));
        MockLogService logservice = new MockLogService();
        HandleregWebApi servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(handlereg, logservice);
        MockHttpServletRequest request = buildGetUrl("/butikker");

        MockHttpServletResponse response = new MockHttpServletResponse();

        loginUser(request, response, "jd", "johnnyBoi");
        servlet.service(request, response);
        assertEquals(200, response.getStatus());
    }

    @Test
    void testLeggTilButikk() throws Exception {
        HandleregService handlereg = mock(HandleregService.class);
        when(handlereg.finnButikker()).thenReturn(Arrays.asList(Butikk.with().build()));
        MockLogService logservice = new MockLogService();
        HandleregWebApi servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(handlereg, logservice);
        MockHttpServletRequest request = buildPostUrl("/nybutikk");
        Butikk butikk = Butikk.with().butikknavn("Ny butikk").build();
        String postBody = mapper.writeValueAsString(butikk);
        request.setBodyContent(postBody);

        MockHttpServletResponse response = new MockHttpServletResponse();

        loginUser(request, response, "jd", "johnnyBoi");
        servlet.service(request, response);
        assertEquals(200, response.getStatus());
    }

    @Test
    void testEndreButikk() throws Exception {
        HandleregService handlereg = mock(HandleregService.class);
        when(handlereg.endreButikk(any())).thenReturn(Arrays.asList(Butikk.with().build()));
        MockLogService logservice = new MockLogService();
        HandleregWebApi servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(handlereg, logservice);
        MockHttpServletRequest request = buildPostUrl("/endrebutikk");
        Butikk butikk = Butikk.with().butikknavn("Ny butikk").build();
        String postBody = mapper.writeValueAsString(butikk);
        request.setBodyContent(postBody);

        MockHttpServletResponse response = new MockHttpServletResponse();

        loginUser(request, response, "jd", "johnnyBoi");
        servlet.service(request, response);
        assertEquals(200, response.getStatus());
    }

    @Test
    void testGetSumOverButikk() throws Exception {
        HandleregService handlereg = mock(HandleregService.class);
        when(handlereg.sumOverButikk()).thenReturn(Arrays.asList(ButikkSum.with().butikk(Butikk.with().butikknavn("Spar Fjellheimen").build()).sum(3345).build(), ButikkSum.with().butikk(Butikk.with().butikknavn("Joker Nord").build()).sum(1234).build()));
        MockLogService logservice = new MockLogService();
        HandleregWebApi servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(handlereg, logservice);
        MockHttpServletRequest request = buildGetUrl("/statistikk/sumbutikk");

        MockHttpServletResponse response = new MockHttpServletResponse();

        loginUser(request, response, "jd", "johnnyBoi");
        servlet.service(request, response);
        assertEquals(200, response.getStatus());
        List<ButikkSum> sumOverButikk = mapper.readValue(getBinaryContent(response), new TypeReference<List<ButikkSum>>() {});
        assertThat(sumOverButikk).isNotEmpty();
        assertEquals("Spar Fjellheimen", sumOverButikk.get(0).getButikk().getButikknavn());
    }

    @Test
    void testGetTotaltHandlebelopPrAar() throws Exception {
        HandleregService handlereg = mock(HandleregService.class);
        when(handlereg.totaltHandlebelopPrAar()).thenReturn(Arrays.asList(SumYear.with().year(Year.of(2016)).sum(45000).build()));
        MockLogService logservice = new MockLogService();
        HandleregWebApi servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(handlereg, logservice);
        MockHttpServletRequest request = buildGetUrl("/statistikk/sumyear");

        MockHttpServletResponse response = new MockHttpServletResponse();

        loginUser(request, response, "jd", "johnnyBoi");
        servlet.service(request, response);
        assertEquals(200, response.getStatus());
        System.out.println("Content:");
        System.out.println(response.getOutputStreamContent());
        List<SumYear> sumOverButikk = mapper.readValue(getBinaryContent(response), new TypeReference<List<SumYear>>() {});
        assertThat(sumOverButikk).isNotEmpty();
        assertEquals(Year.of(2016), sumOverButikk.get(0).getYear());
    }

    @Test
    void testAntallHandlingerIButikk() throws Exception {
        HandleregService handlereg = mock(HandleregService.class);
        when(handlereg.antallHandlingerIButikk()).thenReturn(Arrays.asList(ButikkCount.with().butikk(Butikk.with().butikknavn("Spar Fjellheimen").build()).count(3345).build(), ButikkCount.with().butikk(Butikk.with().butikknavn("Joker Nord").build()).count(1234).build()));
        MockLogService logservice = new MockLogService();
        HandleregWebApi servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(handlereg, logservice);
        MockHttpServletRequest request = buildGetUrl("/statistikk/handlingerbutikk");

        MockHttpServletResponse response = new MockHttpServletResponse();

        loginUser(request, response, "jd", "johnnyBoi");
        servlet.service(request, response);
        assertEquals(200, response.getStatus());
        List<ButikkCount> sumOverButikk = mapper.readValue(getBinaryContent(response), new TypeReference<List<ButikkCount>>() {});
        assertThat(sumOverButikk).isNotEmpty();
        assertEquals("Spar Fjellheimen", sumOverButikk.get(0).getButikk().getButikknavn());
    }

    @Test
    void testSisteHandelIButikk() throws Exception {
        HandleregService handlereg = mock(HandleregService.class);
        when(handlereg.sisteHandelIButikk()).thenReturn(Arrays.asList(ButikkDate.with().butikk(Butikk.with().butikknavn("Spar Fjellheimen").build()).date(new Date()).build(), ButikkDate.with().butikk(Butikk.with().butikknavn("Joker Nord").build()).date(new Date()).build()));
        MockLogService logservice = new MockLogService();
        HandleregWebApi servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(handlereg, logservice);
        MockHttpServletRequest request = buildGetUrl("/statistikk/sistehandel");

        MockHttpServletResponse response = new MockHttpServletResponse();

        loginUser(request, response, "jd", "johnnyBoi");
        servlet.service(request, response);
        assertEquals(200, response.getStatus());
        List<ButikkDate> sumOverButikk = mapper.readValue(getBinaryContent(response), new TypeReference<List<ButikkDate>>() {});
        assertThat(sumOverButikk).isNotEmpty();
        assertEquals("Spar Fjellheimen", sumOverButikk.get(0).getButikk().getButikknavn());
    }

    @Test
    void testTotaltHandlebelopPrAar() throws Exception {
        HandleregService handlereg = mock(HandleregService.class);
        when(handlereg.totaltHandlebelopPrAar()).thenReturn(Arrays.asList(SumYear.with().sum(2345).year(Year.of(2001)).build(), SumYear.with().sum(3241).year(Year.of(2002)).build(), SumYear.with().sum(3241).year(Year.of(2003)).build(), SumYear.with().sum(3241).year(Year.of(2004)).build(), SumYear.with().sum(3241).year(Year.of(2005)).build(), SumYear.with().sum(3241).year(Year.of(2006)).build(), SumYear.with().sum(3241).year(Year.of(2007)).build(), SumYear.with().sum(3241).year(Year.of(2008)).build(), SumYear.with().sum(3241).year(Year.of(2009)).build(), SumYear.with().sum(3241).year(Year.of(2010)).build(), SumYear.with().sum(3241).year(Year.of(2011)).build(), SumYear.with().sum(3241).year(Year.of(2012)).build(), SumYear.with().sum(3241).year(Year.of(2013)).build(), SumYear.with().sum(3241).year(Year.of(2014)).build(), SumYear.with().sum(3241).year(Year.of(2015)).build(), SumYear.with().sum(3241).year(Year.of(2016)).build(), SumYear.with().sum(3241).year(Year.of(2017)).build(), SumYear.with().sum(3241).year(Year.of(2018)).build(), SumYear.with().sum(3241).year(Year.of(2019)).build()));
        MockLogService logservice = new MockLogService();
        HandleregWebApi servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(handlereg, logservice);
        MockHttpServletRequest request = buildGetUrl("/statistikk/sumyear");

        MockHttpServletResponse response = new MockHttpServletResponse();

        loginUser(request, response, "jd", "johnnyBoi");
        servlet.service(request, response);
        assertEquals(200, response.getStatus());
        List<SumYear> sumyear = mapper.readValue(getBinaryContent(response), new TypeReference<List<SumYear>>() {});
        assertThat(sumyear).isNotEmpty();
        assertEquals(Year.of(2001), sumyear.get(0).getYear());
    }

    @Test
    void testTotaltHandlebelopPrAarOgMaaned() throws Exception {
        HandleregService handlereg = mock(HandleregService.class);
        when(handlereg.totaltHandlebelopPrAarOgMaaned()).thenReturn(Arrays.asList(SumYearMonth.with().sum(234).year(Year.of(2001)).month(Month.JANUARY).build(),SumYearMonth.with().sum(234).year(Year.of(2001)).month(Month.FEBRUARY).build(),SumYearMonth.with().sum(234).year(Year.of(2001)).month(Month.MARCH).build(),SumYearMonth.with().sum(234).year(Year.of(2001)).month(Month.APRIL).build(),SumYearMonth.with().sum(234).year(Year.of(2001)).month(Month.MAY).build(),SumYearMonth.with().sum(234).year(Year.of(2001)).month(Month.JUNE).build(),SumYearMonth.with().sum(234).year(Year.of(2001)).month(Month.JULY).build(),SumYearMonth.with().sum(234).year(Year.of(2001)).month(Month.AUGUST).build(), SumYearMonth.with().sum(324).year(Year.of(2002)).month(Month.SEPTEMBER).build(), SumYearMonth.with().sum(324).year(Year.of(2003)).month(Month.OCTOBER).build(), SumYearMonth.with().sum(324).year(Year.of(2004)).month(Month.NOVEMBER).build(), SumYearMonth.with().sum(324).year(Year.of(2005)).month(Month.DECEMBER).build(), SumYearMonth.with().sum(324).year(Year.of(2006)).month(Month.JANUARY).build(), SumYearMonth.with().sum(324).year(Year.of(2007)).month(Month.JANUARY).build(), SumYearMonth.with().sum(324).year(Year.of(2008)).month(Month.JANUARY).build(), SumYearMonth.with().sum(324).year(Year.of(2009)).month(Month.JANUARY).build(), SumYearMonth.with().sum(324).year(Year.of(2010)).month(Month.JANUARY).build(), SumYearMonth.with().sum(324).year(Year.of(2011)).month(Month.JANUARY).build(), SumYearMonth.with().sum(324).year(Year.of(2012)).month(Month.JANUARY).build(), SumYearMonth.with().sum(324).year(Year.of(2013)).month(Month.JANUARY).build(), SumYearMonth.with().sum(324).year(Year.of(2014)).month(Month.JANUARY).build(), SumYearMonth.with().sum(324).year(Year.of(2015)).month(Month.JANUARY).build(), SumYearMonth.with().sum(324).year(Year.of(2016)).month(Month.JANUARY).build(), SumYearMonth.with().sum(324).year(Year.of(2017)).month(Month.JANUARY).build(), SumYearMonth.with().sum(324).year(Year.of(2018)).month(Month.JANUARY).build(), SumYearMonth.with().sum(324).year(Year.of(2019)).month(Month.JANUARY).build()));
        MockLogService logservice = new MockLogService();
        HandleregWebApi servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(handlereg, logservice);
        MockHttpServletRequest request = buildGetUrl("/statistikk/sumyearmonth");

        MockHttpServletResponse response = new MockHttpServletResponse();

        loginUser(request, response, "jd", "johnnyBoi");
        servlet.service(request, response);
        assertEquals(200, response.getStatus());
        List<SumYearMonth> sumyearmonth = mapper.readValue(getBinaryContent(response), new TypeReference<List<SumYearMonth>>() {});
        assertThat(sumyearmonth).isNotEmpty();
        assertEquals(Year.of(2001), sumyearmonth.get(0).getYear());
    }

    @Test
    void testGetFavoritter() throws Exception {
        String username = "jd";
        HandleregService handlereg = mock(HandleregService.class);
        Favoritt favoritt1 = Favoritt.with().favouriteid(1).accountid(1).build();
        Favoritt favoritt2 = Favoritt.with().favouriteid(2).accountid(1).build();
        when(handlereg.finnFavoritter(username)).thenReturn(Arrays.asList(favoritt1, favoritt2));
        MockLogService logservice = new MockLogService();
        HandleregWebApi servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(handlereg, logservice);
        MockHttpServletRequest request = buildGetUrl("/favoritter");
        request.setQueryString("username=" + username);

        MockHttpServletResponse response = new MockHttpServletResponse();

        loginUser(request, response, username, "johnnyBoi");
        servlet.service(request, response);
        assertEquals(200, response.getStatus());
        List<Favoritt> favoritter = mapper.readValue(getBinaryContent(response), new TypeReference<List<Favoritt>>() {});
        assertThat(favoritter).isNotEmpty();
    }

    @Test
    void testPostLeggTilFavoritt() throws Exception {
        String username = "jd";
        HandleregService handlereg = mock(HandleregService.class);
        Favoritt favoritt1 = Favoritt.with().favouriteid(1).accountid(1).build();
        Butikk butikk = Butikk.with().storeId(1).butikknavn("Joker Fjellstu").build();
        Favoritt favoritt2 = Favoritt.with().favouriteid(2).accountid(1).store(butikk).build();
        when(handlereg.leggTilFavoritt(any())).thenReturn(Arrays.asList(favoritt1, favoritt2));
        MockLogService logservice = new MockLogService();
        HandleregWebApi servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(handlereg, logservice);
        MockHttpServletRequest request = buildPostUrl("/favoritt/leggtil");
        NyFavoritt nyFavoritt = NyFavoritt.with().brukernavn(username).butikk(butikk ).build();
        String postBody = mapper.writeValueAsString(nyFavoritt);
        request.setBodyContent(postBody);

        MockHttpServletResponse response = new MockHttpServletResponse();

        loginUser(request, response, username, "johnnyBoi");
        servlet.service(request, response);
        assertEquals(200, response.getStatus());
        List<Favoritt> favoritter = mapper.readValue(getBinaryContent(response), new TypeReference<List<Favoritt>>() {});
        assertThat(favoritter).isNotEmpty();
    }

    @Test
    void testPostSlettFavoritt() throws Exception {
        String username = "jd";
        HandleregService handlereg = mock(HandleregService.class);
        Favoritt favoritt1 = Favoritt.with().favouriteid(1).accountid(1).build();
        Butikk butikk = Butikk.with().storeId(1).butikknavn("Joker Fjellstu").build();
        Favoritt favoritt2 = Favoritt.with().favouriteid(2).accountid(1).store(butikk).build();
        when(handlereg.slettFavoritt(any())).thenReturn(Arrays.asList(favoritt2));
        MockLogService logservice = new MockLogService();
        HandleregWebApi servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(handlereg, logservice);
        MockHttpServletRequest request = buildPostUrl("/favoritt/slett");
        String postBody = mapper.writeValueAsString(favoritt1);
        request.setBodyContent(postBody);

        MockHttpServletResponse response = new MockHttpServletResponse();

        loginUser(request, response, username, "johnnyBoi");
        servlet.service(request, response);
        assertEquals(200, response.getStatus());
        List<Favoritt> favoritter = mapper.readValue(getBinaryContent(response), new TypeReference<List<Favoritt>>() {});
        assertThat(favoritter)
            .isNotEmpty()
            .contains(favoritt2)
            .doesNotContain(favoritt1);
        assertEquals(butikk, favoritter.get(0).getStore());
    }

    @Test
    void testPostFavoritterByttRekkefolge() throws Exception {
        String username = "jd";
        HandleregService handlereg = mock(HandleregService.class);
        Favoritt favoritt1 = Favoritt.with().favouriteid(1).accountid(1).rekkefolge(2).build();
        Butikk butikk = Butikk.with().storeId(1).butikknavn("Joker Fjellstu").build();
        Favoritt favoritt2 = Favoritt.with().favouriteid(2).accountid(1).store(butikk).rekkefolge(1).build();
        when(handlereg.byttRekkefolge(any())).thenReturn(Arrays.asList(favoritt2, favoritt1));
        MockLogService logservice = new MockLogService();
        HandleregWebApi servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(handlereg, logservice);
        MockHttpServletRequest request = buildPostUrl("/favoritter/bytt");
        Favorittpar favoritterSomSkalBytteRekkefolge = Favorittpar.with()
            .forste(favoritt1)
            .andre(favoritt2)
            .build();
        String postBody = mapper.writeValueAsString(favoritterSomSkalBytteRekkefolge);
        request.setBodyContent(postBody);

        MockHttpServletResponse response = new MockHttpServletResponse();

        loginUser(request, response, username, "johnnyBoi");
        servlet.service(request, response);
        assertEquals(200, response.getStatus());
        List<Favoritt> favoritter = mapper.readValue(getBinaryContent(response), new TypeReference<List<Favoritt>>() {});
        assertThat(favoritter)
            .isNotEmpty()
            .containsSequence(favoritt2, favoritt1);
    }

    private byte[] getBinaryContent(MockHttpServletResponse response) throws IOException {
        MockServletOutputStream outputstream = (MockServletOutputStream) response.getOutputStream();
        return outputstream.getBinaryContent();
    }

    private MockHttpServletRequest buildGetUrl(String resource) {
        MockHttpServletRequest request = buildRequest(resource);
        request.setMethod("GET");
        return request;
    }

    private MockHttpServletRequest buildPostUrl(String resource) {
        String contenttype = MediaType.APPLICATION_JSON;
        MockHttpServletRequest request = buildRequest(resource);
        request.setMethod("POST");
        request.setContentType(contenttype);
        request.addHeader("Content-Type", contenttype);
        return request;
    }

    private MockHttpServletRequest buildRequest(String resource) {
        MockHttpSession session = new MockHttpSession();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setProtocol("HTTP/1.1");
        request.setRequestURL("http://localhost:8181/handlereg/api" + resource);
        request.setRequestURI("/handlereg/api" + resource);
        request.setContextPath("/handlereg");
        request.setServletPath("/api");
        request.setSession(session);
        return request;
    }

    private HandleregWebApi simulateDSComponentActivationAndWebWhiteboardConfiguration(HandleregService handlereg, LogService logservice) throws Exception {
        HandleregWebApi servlet = new HandleregWebApi();
        servlet.setLogService(logservice);
        servlet.setHandleregService(handlereg);
        servlet.activate();
        ServletConfig config = createServletConfigWithApplicationAndPackagenameForJerseyResources();
        servlet.init(config);
        return servlet;
    }

    private ServletConfig createServletConfigWithApplicationAndPackagenameForJerseyResources() {
        ServletConfig config = mock(ServletConfig.class);
        when(config.getInitParameterNames()).thenReturn(Collections.enumeration(Arrays.asList(ServerProperties.PROVIDER_PACKAGES)));
        when(config.getInitParameter(ServerProperties.PROVIDER_PACKAGES)).thenReturn("no.priv.bang.handlereg.web.api.resources");
        ServletContext servletContext = mock(ServletContext.class);
        when(servletContext.getContextPath()).thenReturn("/handlereg");
        when(config.getServletContext()).thenReturn(servletContext);
        when(servletContext.getAttributeNames()).thenReturn(Collections.emptyEnumeration());
        return config;
    }
}
