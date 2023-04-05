/*
 * Copyright 2019-2023 Steinar Bang
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
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import static org.osgi.service.http.whiteboard.HttpWhiteboardConstants.*;

import java.io.IOException;
import java.io.InputStream;

import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardContextSelect;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardServletName;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardServletPattern;
import org.osgi.service.log.LogService;
import static javax.servlet.http.HttpServletResponse.*;

import no.priv.bang.servlet.frontend.FrontendServlet;

@Component(service=Servlet.class, immediate=true)
@HttpWhiteboardContextSelect("(" + HTTP_WHITEBOARD_CONTEXT_NAME + "=handlereg)")
@HttpWhiteboardServletName("handlereg")
@HttpWhiteboardServletPattern("/*")
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
            "/login",
            "/unauthorized");
    }

    @Override
    @Reference
    public void setLogService(LogService logservice) {
        super.setLogService(logservice);
    }

    @Override
    protected boolean thisIsAResourceThatShouldBeProcessed(HttpServletRequest request, String pathInfo, String resource, String contentType) {
        return "index.html".equals(resource);
    }

    @Override
    protected void processResource(HttpServletResponse response, HttpServletRequest request, String pathInfo, String resource, String contentType) throws IOException {
        response.setStatus(SC_OK);
        response.setContentType(contentType);
        response.setHeader("Cache-Control", "private, no-store, no-cache, must-revalidate");
        response.setDateHeader("Expires", 0);
        try(ServletOutputStream responseBody = response.getOutputStream()) {
            try(InputStream resourceFromClasspath = getClass().getClassLoader().getResourceAsStream(resource)) {
                copyStream(resourceFromClasspath, responseBody);
            }
        }
    }
}
