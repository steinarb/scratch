/*
 * Copyright 2020 Steinar Bang
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
package no.priv.bang.oldalbum.web.api;

import javax.servlet.Servlet;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.LogService;
import static org.osgi.service.http.whiteboard.HttpWhiteboardConstants.*;

import no.priv.bang.oldalbum.services.OldAlbumService;
import no.priv.bang.servlet.jersey.JerseyServlet;

@Component(
    property= {
        HTTP_WHITEBOARD_SERVLET_PATTERN+"=/api/*",
        HTTP_WHITEBOARD_CONTEXT_SELECT + "=(" + HTTP_WHITEBOARD_CONTEXT_NAME +"=oldalbum)",
        HTTP_WHITEBOARD_SERVLET_NAME+"=oldalbumapi"},
    service=Servlet.class,
    immediate=true
)
public class OldAlbumWebApiServlet extends JerseyServlet {
    private static final long serialVersionUID = -4443311569561233056L;

    @Override
    @Reference
    public void setLogService(LogService logservice) {
        super.setLogService(logservice);
    }

    @Reference
    public void setOldAlbumService(OldAlbumService oldAlbumService) {
        addInjectedOsgiService(OldAlbumService.class, oldAlbumService);
    }

    @Activate
    public void activate() {
        // Called when DS component is activated
    }
}
