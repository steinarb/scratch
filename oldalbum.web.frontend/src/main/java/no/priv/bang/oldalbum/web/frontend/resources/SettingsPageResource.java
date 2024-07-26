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

import static java.lang.String.format;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.apache.shiro.web.util.SavedRequest;
import org.apache.shiro.web.util.WebUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.FormElement;
import org.osgi.service.log.LogService;
import org.osgi.service.log.Logger;

@Path("")
public class SettingsPageResource {

    Logger logger;

    @Inject
    void setLogservice(LogService logservice) {
        this.logger = logservice.getLogger(getClass());
    }

    @Path("settings")
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response getLogin(@QueryParam("originalUri") String originalUri) {
        var originalRequestUri = findOriginalRequestUri().orElse(originalUri);
        var html = loadHtmlFile("pages/settings.html");
        fillFormValues(html, originalRequestUri);

        return Response.status(Response.Status.OK).entity(html.html()).build();
    }

    @POST
    @Path("settings")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public Response postLogin(@FormParam("locale") String locale, @FormParam("originalUri") String originalUri) {
        var localeCookie = new NewCookie("locale", locale); // NOSONAR No secrets in this cookie
        return Response
            .status(Response.Status.FOUND)
            .location(URI.create(notNullUrl(originalUri)))
            .cookie(localeCookie)
            .entity("Settings saved!")
            .build();
    }

    Document loadHtmlFile(String htmlFile) {
        try (InputStream body = getClasspathResource(htmlFile)) {
            return Jsoup.parse(body, "UTF-8", "");
        } catch (IOException e) {
            var message = format("Got exception loading the %s file", htmlFile);
            logger.error(message, e);
            throw new InternalServerErrorException(message);
        }
    }

    InputStream getClasspathResource(String resource) {
        return getClass().getClassLoader().getResourceAsStream(resource);
    }

    private FormElement fillFormValues(Document html, String originalUri) {
        var form = findForm(html);
        updateOriginalUri(form, originalUri);

        return form;
    }

    FormElement findForm(Document html) {
        return (FormElement) html.getElementsByTag("form").get(0);
    }

    void updateOriginalUri(FormElement form, String originalUri) {
        var originalUriHidden = form.select("input[id=originalUri]");
        originalUriHidden.val(originalUri);
    }

    private Optional<String> findOriginalRequestUri() {
        return Optional.ofNullable(WebUtils.getSavedRequest(null))
            .map(SavedRequest::getRequestURI);
    }

    String notNullUrl(String redirectUrl) {
        if (redirectUrl == null) {
            return "";
        }

        return redirectUrl;
    }

}
