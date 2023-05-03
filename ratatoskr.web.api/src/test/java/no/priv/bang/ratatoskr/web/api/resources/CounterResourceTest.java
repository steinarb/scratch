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
package no.priv.bang.ratatoskr.web.api.resources;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import no.priv.bang.ratatoskr.services.RatatoskrService;
import no.priv.bang.ratatoskr.services.beans.CounterBean;
import no.priv.bang.ratatoskr.services.beans.CounterIncrementStepBean;
import no.priv.bang.ratatoskr.web.api.ShiroTestBase;

class CounterResourceTest extends ShiroTestBase {

    @BeforeEach
    void loginUser() {
        loginUser("jad", "1ad");
    }

    @Test
    void testGetCounterIncrementStep() {
        int incrementStepValue = 1;
        RatatoskrService ratatoskr = mock(RatatoskrService.class);
        Optional<CounterIncrementStepBean> optionalIncrementStep = Optional.of(CounterIncrementStepBean.with().counterIncrementStep(incrementStepValue).build());
        when(ratatoskr.getCounterIncrementStep(anyString())).thenReturn(optionalIncrementStep);
        CounterResource resource = new CounterResource();
        resource.ratatoskr = ratatoskr;
        String username = "jad";
        CounterIncrementStepBean bean = resource.getCounterIncrementStep(username);
        assertNotNull(bean);
        assertEquals(incrementStepValue, bean.getCounterIncrementStep());
    }

    @Test
    void testGetCounterIncrementStepWhenNotFound() {
        RatatoskrService ratatoskr = mock(RatatoskrService.class);
        CounterResource resource = new CounterResource();
        resource.ratatoskr = ratatoskr;
        String username = "jad";
        assertThrows(NotFoundException.class, () -> resource.getCounterIncrementStep(username));
    }

    @Test
    void testGetCounterIncrementStepWhenWrongUsername() {
        RatatoskrService ratatoskr = mock(RatatoskrService.class);
        CounterResource resource = new CounterResource();
        resource.ratatoskr = ratatoskr;
        String username = "jod";
        assertThrows(ForbiddenException.class, () -> resource.getCounterIncrementStep(username));
    }

    @Test
    void testGetCounterIncrementStepWhenNoUsername() {
        RatatoskrService ratatoskr = mock(RatatoskrService.class);
        CounterResource resource = new CounterResource();
        resource.ratatoskr = ratatoskr;
        String username = "";
        assertThrows(ForbiddenException.class, () -> resource.getCounterIncrementStep(username));
    }

    @Test
    void testPostCounterIncrementStep() {
        int incrementStepValue = 2;
        RatatoskrService ratatoskr = mock(RatatoskrService.class);
        Optional<CounterIncrementStepBean> optionalIncrementStep = Optional.of(CounterIncrementStepBean.with().counterIncrementStep(incrementStepValue).build());
        when(ratatoskr.updateCounterIncrementStep(any())).thenReturn(optionalIncrementStep);
        CounterResource resource = new CounterResource();
        resource.ratatoskr = ratatoskr;
        String username = "jad";
        CounterIncrementStepBean updateIncrementStep = CounterIncrementStepBean.with()
            .username(username)
            .counterIncrementStep(incrementStepValue)
            .build();
        CounterIncrementStepBean bean = resource.updateCounterIncrementStep(updateIncrementStep);
        assertNotNull(bean);
        assertEquals(incrementStepValue, bean.getCounterIncrementStep());
    }

    @Test
    void testGetCounter() {
        int counterValue = 3;
        RatatoskrService ratatoskr = mock(RatatoskrService.class);
        Optional<CounterBean> counter = Optional.of(CounterBean.with().counter(counterValue).build());
        when(ratatoskr.getCounter(anyString())).thenReturn(counter);
        CounterResource resource = new CounterResource();
        resource.ratatoskr = ratatoskr;
        String username = "jad";
        CounterBean bean = resource.getCounter(username);
        assertNotNull(bean);
        assertEquals(counterValue, bean.getCounter());
    }

    @Test
    void testGetCounterWhenNotFound() {
        RatatoskrService ratatoskr = mock(RatatoskrService.class);
        CounterResource resource = new CounterResource();
        resource.ratatoskr = ratatoskr;
        String username = "jad";
        assertThrows(NotFoundException.class, () -> resource.getCounter(username));
    }

    @Test
    void testIncrementCounter() {
        int counterValue = 3;
        RatatoskrService ratatoskr = mock(RatatoskrService.class);
        Optional<CounterBean> counter = Optional.of(CounterBean.with().counter(counterValue).build());
        when(ratatoskr.incrementCounter(anyString())).thenReturn(counter);
        CounterResource resource = new CounterResource();
        resource.ratatoskr = ratatoskr;
        String username = "jad";
        CounterBean bean = resource.incrementCounter(username);
        assertNotNull(bean);
        assertEquals(counterValue, bean.getCounter());
    }

    @Test
    void testDecrementCounter() {
        int counterValue = 3;
        RatatoskrService ratatoskr = mock(RatatoskrService.class);
        Optional<CounterBean> counter = Optional.of(CounterBean.with().counter(counterValue).build());
        when(ratatoskr.decrementCounter(anyString())).thenReturn(counter);
        CounterResource resource = new CounterResource();
        resource.ratatoskr = ratatoskr;
        String username = "jad";
        CounterBean bean = resource.decrementCounter(username);
        assertNotNull(bean);
        assertEquals(counterValue, bean.getCounter());
    }

}
