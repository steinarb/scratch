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
package no.priv.bang.handlereg.web.frontend;

import javax.servlet.Servlet;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.service.log.LogService;

import no.priv.bang.servlet.frontend.FrontendServlet;

@Component(
    property= {
        HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN+"=/*",
        HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT + "=(" + HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME +"=handlereg)",
        HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME+"=handlereg"},
    service=Servlet.class,
    immediate=true
)
public class HandleregServlet extends FrontendServlet {
    private static final long serialVersionUID = -3496606785818930881L;

    public HandleregServlet() {
        super();
        // The paths used by the react router
        setRoutes(
            "/",
            "/hurtigregistrering",
            "/statistikk",
            "/statistikk/sumbutikk",
            "/statistikk/handlingerbutikk",
            "/statistikk/sistehandel",
            "/statistikk/sumyear",
            "/statistikk/sumyearmonth",
            "/favoritter",
            "/favoritter/leggtil",
            "/favoritter/slett",
            "/favoritter/sorter",
            "/nybutikk",
            "/endrebutikk",
            "/login");
    }

    @Override
    @Reference
    public void setLogService(LogService logservice) {
        super.setLogService(logservice);
    }
}
