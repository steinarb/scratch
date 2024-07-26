/*
 * Copyright 2024 Steinar Bang
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
package no.priv.bang.oldalbum.web.frontend.resources;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.InternalServerErrorException;

import org.junit.jupiter.api.Test;

import no.priv.bang.osgi.service.mocks.logservice.MockLogService;

class SettingsPageResourceTest {

    @Test
    void testLoadHtmlFileOnNonExistingFile() throws Exception {
        var logservice = new MockLogService();
        var resource = spy(new SettingsPageResource());
        var streamThrowingException = mock(InputStream.class);
        when(streamThrowingException.read(any(byte[].class), anyInt(), anyInt())).thenThrow(IOException.class);
        when(resource.getClasspathResource(anyString())).thenReturn(streamThrowingException);
        resource.setLogservice(logservice);

        assertThat(logservice.getLogmessages()).isEmpty(); // check precondition
        var e = assertThrows(InternalServerErrorException.class, () -> resource.loadHtmlFile("nonexisting.html"));

        assertThat(logservice.getLogmessages()).isNotEmpty();
        assertThat(e.getMessage()).startsWith("Got exception loading the");
    }

    @Test
    void testNotNullUrl() {
        var resource = new SettingsPageResource();
        var url = "http://localhost:8181/oldalbum/moto/vfr96/fjell2";

        assertThat(resource.notNullUrl(url)).isEqualTo(url);
    }

}
