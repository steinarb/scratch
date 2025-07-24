/*
 * Copyright 2018-2025 Steinar Bang
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
package no.priv.bang.demos.hellokaraf;

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardServletPattern;
import org.osgi.service.log.LogService;

import no.priv.bang.osgi.service.adapters.logservice.LoggerAdapter;

@Component(service={Servlet.class})
@HttpWhiteboardServletPattern("/hello")
public class HelloServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final LoggerAdapter logger = new LoggerAdapter(getClass());
    private static final String TITLE = "Hello world!";
    private static final String PARAGRAPH = "This is sent via PAX Web Whiteboard Extender.";

    @Reference
    public void setLogservice(LogService logservice) {
        this.logger.setLogService(logservice);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            response.setContentType("text/html");
            try(var template = getClass().getClassLoader().getResourceAsStream("index.html")) {
                var html = Jsoup.parse(template, "UTF-8", "");
                html.select("title").get(0).text(TITLE);
                html.select("h1").get(0).text(TITLE);
                html.select("p").get(0).text(PARAGRAPH);
                try(var writer = response.getWriter()) {
                    writer.print(html.toString());
                }
            }

            response.setStatus(200);
        } catch (Exception e) {
            logger.error("Hello servlet caught exception ", e);
            response.setStatus(500); // Report internal server error
        }
    }

}
