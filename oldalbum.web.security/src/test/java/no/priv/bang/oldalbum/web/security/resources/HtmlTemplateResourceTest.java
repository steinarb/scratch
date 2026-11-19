package no.priv.bang.oldalbum.web.security.resources;
/*
 * Copyright 2025-2026 Steinar Bang
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.InternalServerErrorException;

import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;

class HtmlTemplateResourceTest {

    @Test
    void testTemplateFileNotFound() {
        class NotFoundFile extends HtmlTemplateResource {
            Document expandFile() {
                return loadHtmlFile("Not found file");
            }

            @Override
            InputStream getClasspathResource(String resource) {
                var mockStream = mock(InputStream.class);
                try { when(mockStream.read(any(), anyInt(), anyInt())).thenThrow(IOException.class); } catch (IOException e) {/* Empty */}
                return mockStream;
            }


        }

        var resource = new NotFoundFile();
        assertThrows(InternalServerErrorException.class, resource::expandFile);
    }

}
