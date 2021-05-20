/*
 * Copyright 2020-2021 Steinar Bang
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
package no.priv.bang.oldalbum.web.frontend;

import javax.servlet.Servlet;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static javax.servlet.http.HttpServletResponse.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import static org.osgi.service.http.whiteboard.HttpWhiteboardConstants.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.osgi.service.log.LogService;import no.priv.bang.oldalbum.services.OldAlbumService;
import no.priv.bang.oldalbum.services.bean.AlbumEntry;
import no.priv.bang.servlet.frontend.FrontendServlet;

@Component(
    property= {
        HTTP_WHITEBOARD_SERVLET_PATTERN+"=/*",
        HTTP_WHITEBOARD_CONTEXT_SELECT + "=(" + HTTP_WHITEBOARD_CONTEXT_NAME +"=oldalbum)",
        HTTP_WHITEBOARD_SERVLET_NAME+"=oldalbum"},
    service=Servlet.class,
    immediate=true
)
public class OldalbumServlet extends FrontendServlet {
    private static final long serialVersionUID = -2378206477575636399L;
    private OldAlbumService oldalbum; // NOSONAR set by OSGi dependency injection and not touched after that

    public OldalbumServlet() {
        super();
        setRoutes(
            "/login",
            "/modifyalbum",
            "/addalbum",
            "/modifypicture",
            "/addpicture"
                  );
    }

    @Override
    @Reference
    public void setLogService(LogService logservice) {
        super.setLogService(logservice);
    }

    @Reference
    public void setOldalbumService(OldAlbumService oldalbum) {
        this.oldalbum = oldalbum;
    }

    @Activate
    public void activate() {
        // Called when the DS component is activated
    }

    @Override
    public List<String> getRoutes() {
        return combineDynamicAndStaticRoutes();
    }

    private List<String> combineDynamicAndStaticRoutes() {
        List<String> dynamicroutes = oldalbum.getPaths();
        List<String> staticroutes = super.getRoutes();
        int numberOfRoutes = dynamicroutes.size() + staticroutes.size();
        List<String> allroutes = new ArrayList<>(numberOfRoutes);
        allroutes.addAll(dynamicroutes);
        allroutes.addAll(staticroutes);
        return allroutes;
    }

    @Override
    protected boolean thisIsAResourceThatShouldBeProcessed(HttpServletRequest request, String pathInfo, String resource, String contentType) {
        return "index.html".equals(resource);
    }

    @Override
    protected void processResource(HttpServletResponse response, HttpServletRequest request, String pathInfo, String resource, String contentType) throws IOException {
        AlbumEntry entry = oldalbum.getAlbumEntryFromPath(pathInfo);
        response.setStatus(SC_OK);
        response.setContentType(contentType);
        setLastModifiedHeader(response, entry);
        Document html = loadHtmlFile(resource);
        addMetaTagIfNotEmpty(html, "og:url", request.getRequestURL().toString());
        addOpenGraphHeaderElements(html, entry);
        html.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        try(ServletOutputStream body = response.getOutputStream()) {
            body.print(html.outerHtml());
        }
    }

    private void setLastModifiedHeader(HttpServletResponse response, AlbumEntry entry) {
        if (entry != null && entry.getLastModified() != null) {
            response.setDateHeader("Last-Modified", entry.getLastModified().toInstant().toEpochMilli());
        }
    }

    void addOpenGraphHeaderElements(Document html, AlbumEntry entry) {
        if (entry != null) {
            setTitleIfNotEmpty(html, entry.getTitle());
            setDescriptionIfNotEmpty(html, entry.getDescription());
            addMetaTagIfNotEmpty(html, "og:title", entry.getTitle());
            addMetaTagIfNotEmpty(html, "og:description", entry.getDescription());
            addMetaTagIfNotEmpty(html, "og:image", entry.getImageUrl());
            addMetaTagIfNotEmpty(html, "twitter:card", "summary_large_image");
            addMetaTagIfNotEmpty(html, "twitter:title", entry.getTitle());
            addMetaTagIfNotEmpty(html, "twitter:description", entry.getDescription());
            addMetaTagIfNotEmpty(html, "twitter:image", entry.getImageUrl());
            if (entry.isAlbum()) {
                List<AlbumEntry> children = oldalbum.getChildren(entry.getId());
                if (!children.isEmpty()) {
                    for(AlbumEntry child : children) {
                        addMetaTagIfNotEmpty(html, "og:image", child.getImageUrl());
                    }
                }
            }
        }

    }

    private void setTitleIfNotEmpty(Document html, String title) {
        if (!nullOrEmpty(title)) {
            Elements titles = html.head().getElementsByTag("title");
            titles.first().text(title);
        }
    }

    private void setDescriptionIfNotEmpty(Document html, String description) {
        if (!nullOrEmpty(description)) {
            html.head().appendElement("meta").attr("name", "description").attr("content", description).attr("data-react-helmet", "true");
        }
    }

    protected void addMetaTagIfNotEmpty(Document html, String property, String content) {
        String propertyAttribute = property.startsWith("twitter:") ? "name" : "property";
        if (content != null && !content.isEmpty()) {
            html.head().appendElement("meta").attr(propertyAttribute, property).attr("content", content);
        }
    }

    protected Document loadHtmlFile(String htmlFile) throws IOException {
        try (InputStream body = getClasspathResource(htmlFile)) {
            return Jsoup.parse(body, "UTF-8", "");
        }
    }

    InputStream getClasspathResource(String resource) {
        return getClass().getClassLoader().getResourceAsStream(resource);
    }

    static boolean nullOrEmpty(String s) {
        return s == null || s.isBlank();
    }

}
