/*
 * Copyright 2019-2021 Steinar Bang
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
package no.priv.bang.handlereg.web.api.resources;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import java.time.Month;
import java.time.Year;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;

import no.priv.bang.handlereg.services.Butikk;
import no.priv.bang.handlereg.services.ButikkCount;
import no.priv.bang.handlereg.services.ButikkDate;
import no.priv.bang.handlereg.services.ButikkSum;
import no.priv.bang.handlereg.services.HandleregService;
import no.priv.bang.handlereg.services.SumYear;
import no.priv.bang.handlereg.services.SumYearMonth;
import no.priv.bang.osgi.service.mocks.logservice.MockLogService;

class StatistikkResourceTest {

    @Test
    void testGetSumOverButikk() {
        HandleregService handlereg = mock(HandleregService.class);
        when(handlereg.sumOverButikk()).thenReturn(Arrays.asList(ButikkSum.with().butikk(Butikk.with().butikknavn("Spar Fjellheimen").build()).sum(3345).build(), ButikkSum.with().butikk(Butikk.with().butikknavn("Joker Nord").build()).sum(1234).build()));
        MockLogService logservice = new MockLogService();
        StatistikkResource resource = new StatistikkResource();
        resource.handlereg = handlereg;
        resource.logservice = logservice;

        List<ButikkSum> sumOverButikk = resource.sumOverButikk();
        assertThat(sumOverButikk).isNotEmpty();
        assertEquals("Spar Fjellheimen", sumOverButikk.get(0).getButikk().getButikknavn());
    }

    @Test
    void testAntallHandlingerIButikk() {
        HandleregService handlereg = mock(HandleregService.class);
        when(handlereg.antallHandlingerIButikk()).thenReturn(Arrays.asList(ButikkCount.with().butikk(Butikk.with().butikknavn("Spar Fjellheimen").build()).count(3345).build(), ButikkCount.with().butikk(Butikk.with().butikknavn("Joker Nord").build()).count(1234).build()));
        MockLogService logservice = new MockLogService();
        StatistikkResource resource = new StatistikkResource();
        resource.handlereg = handlereg;
        resource.logservice = logservice;

        List<ButikkCount> antallHandlingerIButikk = resource.antallHandlingerIButikk();
        assertThat(antallHandlingerIButikk).isNotEmpty();
        assertEquals("Spar Fjellheimen", antallHandlingerIButikk.get(0).getButikk().getButikknavn());
    }

    @Test
    void testSisteHandelIButikk() {
        HandleregService handlereg = mock(HandleregService.class);
        when(handlereg.sisteHandelIButikk()).thenReturn(Arrays.asList(ButikkDate.with().butikk(Butikk.with().butikknavn("Spar Fjellheimen").build()).date(new Date()).build(), ButikkDate.with().butikk(Butikk.with().butikknavn("Joker Nord").build()).date(new Date()).build()));
        MockLogService logservice = new MockLogService();
        StatistikkResource resource = new StatistikkResource();
        resource.handlereg = handlereg;
        resource.logservice = logservice;

        List<ButikkDate> sisteHandelIButikk = resource.sisteHandelIButikk();
        assertThat(sisteHandelIButikk).isNotEmpty();
        assertEquals("Spar Fjellheimen", sisteHandelIButikk.get(0).getButikk().getButikknavn());
    }

    @Test
    void testTotaltHandlebelopPrAar() {
        HandleregService handlereg = mock(HandleregService.class);
        when(handlereg.totaltHandlebelopPrAar()).thenReturn(Arrays.asList(SumYear.with().sum(2345).year(Year.of(2001)).build(), SumYear.with().sum(3241).year(Year.of(2002)).build(), SumYear.with().sum(3241).year(Year.of(2003)).build(), SumYear.with().sum(3241).year(Year.of(2004)).build(), SumYear.with().sum(3241).year(Year.of(2005)).build(), SumYear.with().sum(3241).year(Year.of(2006)).build(), SumYear.with().sum(3241).year(Year.of(2007)).build(), SumYear.with().sum(3241).year(Year.of(2008)).build(), SumYear.with().sum(3241).year(Year.of(2009)).build(), SumYear.with().sum(3241).year(Year.of(2010)).build(), SumYear.with().sum(3241).year(Year.of(2011)).build(), SumYear.with().sum(3241).year(Year.of(2012)).build(), SumYear.with().sum(3241).year(Year.of(2013)).build(), SumYear.with().sum(3241).year(Year.of(2014)).build(), SumYear.with().sum(3241).year(Year.of(2015)).build(), SumYear.with().sum(3241).year(Year.of(2016)).build(), SumYear.with().sum(3241).year(Year.of(2017)).build(), SumYear.with().sum(3241).year(Year.of(2018)).build(), SumYear.with().sum(3241).year(Year.of(2019)).build()));
        MockLogService logservice = new MockLogService();
        StatistikkResource resource = new StatistikkResource();
        resource.handlereg = handlereg;
        resource.logservice = logservice;

        List<SumYear> totaltHandlebelopPrAar = resource.totaltHandlebelopPrAar();
        assertThat(totaltHandlebelopPrAar).isNotEmpty();
        assertEquals(Year.of(2001), totaltHandlebelopPrAar.get(0).getYear());
    }

    @Test
    void testTotaltHandlebelopPrAarOgMaaned() {
        HandleregService handlereg = mock(HandleregService.class);
        when(handlereg.totaltHandlebelopPrAarOgMaaned()).thenReturn(Arrays.asList(SumYearMonth.with().sum(234).year(Year.of(2001)).month(Month.JANUARY).build(),SumYearMonth.with().sum(234).year(Year.of(2001)).month(Month.FEBRUARY).build(),SumYearMonth.with().sum(234).year(Year.of(2001)).month(Month.MARCH).build(),SumYearMonth.with().sum(234).year(Year.of(2001)).month(Month.APRIL).build(),SumYearMonth.with().sum(234).year(Year.of(2001)).month(Month.MAY).build(),SumYearMonth.with().sum(234).year(Year.of(2001)).month(Month.JUNE).build(),SumYearMonth.with().sum(234).year(Year.of(2001)).month(Month.JULY).build(),SumYearMonth.with().sum(234).year(Year.of(2001)).month(Month.AUGUST).build(), SumYearMonth.with().sum(324).year(Year.of(2002)).month(Month.SEPTEMBER).build(), SumYearMonth.with().sum(324).year(Year.of(2003)).month(Month.OCTOBER).build(), SumYearMonth.with().sum(324).year(Year.of(2004)).month(Month.NOVEMBER).build(), SumYearMonth.with().sum(324).year(Year.of(2005)).month(Month.DECEMBER).build(), SumYearMonth.with().sum(324).year(Year.of(2006)).month(Month.JANUARY).build(), SumYearMonth.with().sum(324).year(Year.of(2007)).month(Month.JANUARY).build(), SumYearMonth.with().sum(324).year(Year.of(2008)).month(Month.JANUARY).build(), SumYearMonth.with().sum(324).year(Year.of(2009)).month(Month.JANUARY).build(), SumYearMonth.with().sum(324).year(Year.of(2010)).month(Month.JANUARY).build(), SumYearMonth.with().sum(324).year(Year.of(2011)).month(Month.JANUARY).build(), SumYearMonth.with().sum(324).year(Year.of(2012)).month(Month.JANUARY).build(), SumYearMonth.with().sum(324).year(Year.of(2013)).month(Month.JANUARY).build(), SumYearMonth.with().sum(324).year(Year.of(2014)).month(Month.JANUARY).build(), SumYearMonth.with().sum(324).year(Year.of(2015)).month(Month.JANUARY).build(), SumYearMonth.with().sum(324).year(Year.of(2016)).month(Month.JANUARY).build(), SumYearMonth.with().sum(324).year(Year.of(2017)).month(Month.JANUARY).build(), SumYearMonth.with().sum(324).year(Year.of(2018)).month(Month.JANUARY).build(), SumYearMonth.with().sum(324).year(Year.of(2019)).month(Month.JANUARY).build()));
        MockLogService logservice = new MockLogService();
        StatistikkResource resource = new StatistikkResource();
        resource.handlereg = handlereg;
        resource.logservice = logservice;

        List<SumYearMonth> totaltHandlebelopPrAarOgMaaned = resource.totaltHandlebelopPrAarOgMaaned();
        assertThat(totaltHandlebelopPrAarOgMaaned).isNotEmpty();
        assertEquals(Year.of(2001), totaltHandlebelopPrAarOgMaaned.get(0).getYear());
    }

}
