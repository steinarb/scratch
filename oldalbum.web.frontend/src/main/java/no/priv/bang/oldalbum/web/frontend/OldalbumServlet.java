/*
 * Copyright 2020-2024 Steinar Bang
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
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.UriBuilder;

import static java.lang.String.format;
import static java.util.List.of;
import static java.util.Optional.ofNullable;
import static javax.servlet.http.HttpServletResponse.*;

import org.apache.shiro.SecurityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardContextSelect;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardServletName;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardServletPattern;

import static org.osgi.service.http.whiteboard.HttpWhiteboardConstants.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import org.osgi.service.log.LogService;import no.priv.bang.oldalbum.services.OldAlbumService;
import no.priv.bang.oldalbum.services.bean.AlbumEntry;
import no.priv.bang.servlet.frontend.FrontendServlet;

@Component(service=Servlet.class, immediate=true)
@HttpWhiteboardContextSelect("(" + HTTP_WHITEBOARD_CONTEXT_NAME + "=oldalbum)")
@HttpWhiteboardServletName("oldalbum")
@HttpWhiteboardServletPattern("/*")
public class OldalbumServlet extends FrontendServlet {
    private static final String CLASS = "class";
    private static final long serialVersionUID = -2378206477575636399L;
    private OldAlbumService oldalbum; // NOSONAR set by OSGi dependency injection and not touched after that

    public OldalbumServlet() {
        super();
        setRoutes(
            "/login",
            "/unauthorized",
            "/modifyalbum",
            "/addalbum",
            "/modifypicture",
            "/addpicture");
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
        var subject = SecurityUtils.getSubject();
        var isLoggedIn = subject.isAuthenticated() || subject.isRemembered();
        var dynamicroutes = oldalbum.getPaths(isLoggedIn);
        var staticroutes = super.getRoutes();
        var numberOfRoutes = dynamicroutes.size() + staticroutes.size();
        var allroutes = new ArrayList<String>(numberOfRoutes);
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
        var entry = oldalbum.getAlbumEntryFromPath(pathInfo);
        response.setStatus(SC_OK);
        response.setContentType(contentType);
        setLastModifiedHeader(response, entry);
        var html = loadHtmlFile(resource);
        addMetaTagIfNotEmpty(html, "og:url", request.getRequestURL().toString());
        addOpenGraphHeaderElements(html, entry);
        renderEntry(request, html, entry);
        html.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        try(var body = response.getOutputStream()) {
            body.print(html.outerHtml());
        }
    }

    @Override
    protected void handleResourceNotFound(HttpServletRequest request, HttpServletResponse response, String resource) throws IOException {
        response.setStatus(SC_NOT_FOUND);
        response.setContentType("text/html");
        var html = loadHtmlFile("index.html");
        replaceRootWithNotFoundMessage(html, request);
        try(var responseBody = response.getOutputStream()) {
            responseBody.print(html.outerHtml());
        }
    }

    private void setLastModifiedHeader(HttpServletResponse response, AlbumEntry entry) {
        if (entry != null && entry.lastModified() != null) {
            response.setDateHeader("Last-Modified", entry.lastModified().toInstant().toEpochMilli());
        }
    }

    void addOpenGraphHeaderElements(Document html, AlbumEntry entry) {
        if (entry != null) {
            setTitleIfNotEmpty(html, entry.title());
            setDescriptionIfNotEmpty(html, entry.description());
            addMetaTagIfNotEmpty(html, "og:title", entry.title());
            addMetaTagIfNotEmpty(html, "og:description", entry.description());
            addMetaTagIfNotEmpty(html, "og:image", entry.imageUrl());
            addMetaTagIfNotEmpty(html, "twitter:card", "summary_large_image");
            addMetaTagIfNotEmpty(html, "twitter:title", entry.title());
            addMetaTagIfNotEmpty(html, "twitter:description", entry.description());
            addMetaTagIfNotEmpty(html, "twitter:image", entry.imageUrl());
            if (entry.album()) {
                var children = oldalbum.getChildren(entry.id(), false);
                if (!children.isEmpty()) {
                    for(AlbumEntry child : children) {
                        addMetaTagIfNotEmpty(html, "og:image", child.imageUrl());
                    }
                }
            }
        }

    }

    private void setTitleIfNotEmpty(Document html, String title) {
        if (!nullOrEmpty(title)) {
            var titles = html.head().getElementsByTag("title");
            titles.first().text(title);
        }
    }

    private void setDescriptionIfNotEmpty(Document html, String description) {
        if (!nullOrEmpty(description)) {
            html.head().appendElement("meta").attr("name", "description").attr("content", description).attr("data-react-helmet", "true");
        }
    }

    protected void addMetaTagIfNotEmpty(Document html, String property, String content) {
        var propertyAttribute = property.startsWith("twitter:") ? "name" : "property";
        if (content != null && !content.isEmpty()) {
            html.head().appendElement("meta").attr(propertyAttribute, property).attr("content", content);
        }
    }

    private void replaceRootWithNotFoundMessage(Document html, HttpServletRequest request) {
        var root = html.body().getElementsByAttributeValue("id", "root").first();
        root.appendChild(title("Not found!")
            .appendChild(navWithUpLink(request))
            .appendChild(new Element("p").appendText("The web page you were looking for cannot be found.")));
    }

    void renderEntry(HttpServletRequest request, Document html, AlbumEntry entry) {
        ofNullable(entry).ifPresent(e -> {
            if (!e.album()) {
                renderPicture(request, html, e);
            } else {
                renderAlbum(request, html, e);
            }
        });
    }

    void renderPicture(HttpServletRequest request, Document html, AlbumEntry entry) {
        var root = html.body().getElementsByAttributeValue("id", "root").first();
        root.appendChild(title(entry))
            .appendChild(navigationLinks(request, entry))
            .appendChild(img(entry))
            .appendChild(description(entry));
    }

    void renderAlbum(HttpServletRequest request, Document html, AlbumEntry entry) {
        var root = html.body().getElementsByAttributeValue("id", "root").first();
        root.appendChild(title(entry))
            .appendChild(navigationLinks(request, entry))
            .appendChild(description(entry))
            .appendChild(thumbnails(request, entry));
    }

    Element title(AlbumEntry entry) {
        return title(ofNullable(entry.title())
            .map(t -> t.isBlank() ? findLastPartOfPath(entry) : t)
            .orElseGet(() -> findLastPartOfPath(entry)));
    }

    Element title(String text) {
        return new Element("h1").attr(CLASS, "image-title").appendText(text);
    }

    Element navWithUpLink(HttpServletRequest request) {
        var top = request.getContextPath() + "/";
        var navigationLinks = new Element("ul");
        navigationLinks.appendChild(new Element("li").appendChild(new Element("a").attr("href", top).appendText("Up")));
        return new Element("nav").attr(CLASS, "image-navbar").appendChild(navigationLinks);
    }

    Element navigationLinks(HttpServletRequest request, AlbumEntry entry) {
        String locale = getLocale(request);
        var up = oldalbum.displayText("up", locale);
        var prev = oldalbum.displayText("previous", locale);
        var next = oldalbum.displayText("next", locale);
        var navigationLinks = new Element("ul");
        var servletContextPath = findServletContext(request, entry);
        oldalbum.getAlbumEntry(entry.parent()).ifPresent(parent -> {
            var fragment = findLastPartOfPath(entry);
            navigationLinks.appendChild(new Element("li").appendChild(new Element("a").attr("href", servletContextPath + parent.path() + "#" + fragment).appendText(up)));
        });
        var isLoggedIn = SecurityUtils.getSubject().isAuthenticated();
        oldalbum.getPreviousAlbumEntry(entry.id(), isLoggedIn).ifPresent(previous -> navigationLinks.appendChild(new Element("li").appendChild(new Element("a").attr("href", servletContextPath + previous.path()).appendText(prev))));
        oldalbum.getNextAlbumEntry(entry.id(), isLoggedIn).ifPresent(nextItem -> navigationLinks.appendChild(new Element("li").appendChild(new Element("a").attr("href", servletContextPath + nextItem.path()).appendText(next))));
        if (entryIsPictureOrAlbumWithPictures(entry)) {
            navigationLinks.appendChild(new Element("li").appendChild(downloadLink(entry, locale, servletContextPath)));
        }
        navigationLinks.appendChild(new Element("li").appendChild(settingsLink(request, servletContextPath)));
        navigationLinks.appendChild(new Element("li").attr(CLASS, "float-right").appendChild(loginLink(request, locale, entry)));
        return new Element("nav").attr(CLASS, "image-navbar").appendChild(navigationLinks);
    }

    Element downloadLink(AlbumEntry entry, String locale, String servletContextPath) {
        var label = entry.album() ? oldalbum.displayText("downloadalbum", locale) : oldalbum.displayText("downloadpicture", locale);
        var url = format("%s/api/image/download/%s", servletContextPath, entry.id());
        var downloadName = entry.album() ? findLastPartOfPath(entry) + ".zip" : findLastPartOfPath(entry) + ".jpg";
        return new Element("a").attr("href", url).attr("download", downloadName).attr("target", "_blank").attr("rel", "noopener noreferrer")
            .appendChild(new Element("span").attr(CLASS, "oi oi-data-transfer-download").attr("title", "data transfer download").attr("aria-hidden", "true"))
            .appendText(label);
    }

    Element settingsLink(HttpServletRequest request, String servletContextPath) {
        var originalUrl = urlEncode(request.getRequestURL().toString());
        var settingsUrl = UriBuilder.fromPath(servletContextPath + "/pages/settings").queryParam("originalUri", originalUrl).build().toASCIIString();
        return new Element("a").attr("href", settingsUrl).appendText("Settings");
    }

    Element loginLink(HttpServletRequest request, String locale, AlbumEntry entry) {
        var loginText =  oldalbum.displayText("login", locale);
        var originalUrl = urlEncode(request.getRequestURL().toString());
        var servletContext = findServletContext(request, entry);
        var loginUrl = UriBuilder.fromPath(servletContext + "/auth/login").queryParam("originalUri", originalUrl).build().toASCIIString();
        return new Element("a").attr("href", loginUrl).appendText(loginText);
    }

    Element img(AlbumEntry entry) {
        return new Element("img").attr(CLASS, "image-responsive").attr("src", entry.imageUrl());
    }

    Element description(AlbumEntry entry) {
        var descriptionText = ofNullable(entry.description()).orElse("");
        var dateAndSizeText = formatDateAndSize(entry);
        if (descriptionText.isBlank() && dateAndSizeText.isBlank()) {
            return new Element("div");
        }

        var description = new Element("p")
            .attr(CLASS, "image-description")
            .appendText(descriptionText);
        var dateAndSize = new Element("p").appendText(dateAndSizeText);
        return new Element("div")
            .attr(CLASS, "image-description-box")
            .appendChild(description)
            .appendChild(dateAndSize);

    }

    Element thumbnails(HttpServletRequest request, AlbumEntry entry) {
        var div = new Element("ul").attr(CLASS, "thumbnail-list");
        var servletContextPath = findServletContext(request, entry);
        var isLoggedIn = SecurityUtils.getSubject().isAuthenticated();
        for (var child : oldalbum.getChildren(entry.id(), isLoggedIn)) {
            div.appendChild(thumbnail(servletContextPath, child));
        }

        return div;
    }

    Element thumbnail(String servletContextPath, AlbumEntry child) {
        var resourceName = findLastPartOfPath(child);
        var fullSizeThumbnail = child.album() ? isNullOrBlank(findFirstImageInAlbumChild(child)) : isNullOrBlank(child.thumbnailUrl());
        var thumbnailUrl = child.album() ?
            findFirstThumbnailOrFullSizeImageInAlbumChild(child, fullSizeThumbnail) :
            findThumbnailOrFullSizeImage(child, fullSizeThumbnail);
        var thumbnailClass = fullSizeThumbnail ? "album-item-fullsize-thumbnail" : "album-item-thumbnail";
        var img = new Element("img")
            .attr(CLASS, thumbnailClass)
            .attr("src", thumbnailUrl);
        var thumbnailImage = new Element("div").appendChild(img);
        var titleText = !isNullOrBlank(child.title()) ? child.title() : resourceName;
        var title = new Element("h3").appendText(titleText);
        var description = new Element("p")
            .attr(CLASS, "album-item-description")
            .appendText(ofNullable(child.description()).orElse(""));
        var dateAndSize = new Element("p").appendText(formatDateAndSize(child));
        var sub = new Element("div").appendChild(description).appendChild(dateAndSize);
        var text = new Element("div")
            .attr(CLASS, "album-item-text")
            .appendChild(title)
            .appendChild(sub);
        var a = new Element("a")
            .attr(CLASS, "album-item-link")
            .attr("href", servletContextPath + child.path())
            .attr("name", resourceName)
            .appendChild(thumbnailImage)
            .appendChild(text);
        return new Element("li").attr(CLASS, "album-item").appendChild(a);
    }

    String findThumbnailOrFullSizeImage(AlbumEntry child, boolean fullSizeThumbnail) {
        return fullSizeThumbnail ? child.imageUrl() : child.thumbnailUrl();
    }

    String findFirstThumbnailOrFullSizeImageInAlbumChild(AlbumEntry child, boolean fullSizeThumbnail) {
        return fullSizeThumbnail ? findFirstFullsizeImageInAlbumChild(child) : findFirstImageInAlbumChild(child);
    }

    boolean isNullOrBlank(String text) {
        return ofNullable(text).map(String::isBlank).orElse(true);
    }

    String findLastPartOfPath(AlbumEntry child) {
        return ofNullable(child.path()).map(path -> {
            var elements = path.split("/");
            return (elements.length > 0) ? elements[elements.length - 1] : "";
        }).orElse("");
    }

    String findFirstImageInAlbumChild(AlbumEntry child) {
        var children = oldalbum.getChildren(child.id(), false);
        var firstImage = children.stream().filter(a -> !a.album() && !isNullOrBlank(a.thumbnailUrl())).findFirst();
        if (firstImage.isPresent()) {
            return firstImage.get().thumbnailUrl();
        }

        return children.stream().filter(a -> a.album()).findFirst().map(this::findFirstImageInAlbumChild).orElse("");
    }

    String findFirstFullsizeImageInAlbumChild(AlbumEntry child) {
        var children = oldalbum.getChildren(child.id(), false);
        var firstImage = children.stream().filter(a -> !a.album() && !isNullOrBlank(a.imageUrl())).findFirst();
        if (firstImage.isPresent()) {
            return firstImage.get().imageUrl();
        }

        return children.stream().filter(a -> a.album()).findFirst().map(this::findFirstFullsizeImageInAlbumChild).orElse("");
    }

    String formatDateAndSize(AlbumEntry child) {
        var lastModifiedDate = ofNullable(child.lastModified()).map(lastModified -> format("%tF ", lastModified)).orElse("");
        var contentLength = child.contentLength() > 0 ? child.contentLength() : 0;
        if (contentLength / 1000000.0 > 1) {
            return format("%s%dMB", lastModifiedDate, Math.round(contentLength / 1000000.0));
        } else if (contentLength / 1000.0 > 1) {
            return format("%s%dkB", lastModifiedDate, Math.round(contentLength / 1000.0));
        } else if (contentLength > 0) {
            return format("%s%dB", lastModifiedDate, contentLength);
        } else {
            return lastModifiedDate;
        }
    }

    protected Document loadHtmlFile(String htmlFile) throws IOException {
        try (var body = getClasspathResource(htmlFile)) {
            return Jsoup.parse(body, "UTF-8", "");
        }
    }

    InputStream getClasspathResource(String resource) {
        return getClass().getClassLoader().getResourceAsStream(resource);
    }

    boolean entryIsPictureOrAlbumWithPictures(AlbumEntry entry) {
        if (entry.album()) {
            var children = oldalbum.getChildren(entry.id(), false);
            for (var child : children) {
                if (!child.album()) {
                    return true;
                }
            }

            return false;
        } else {
            return true;
        }
    }

    static String findServletContext(HttpServletRequest request, AlbumEntry entry) {
        return "/".equals(entry.path()) ? request.getRequestURI().replaceAll("/$", "") : request.getRequestURI().replace(entry.path(), "");
    }

    static String urlEncode(String stringToEncode) {
        try {
            return URLEncoder.encode(stringToEncode, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            sneakyThrows(e); // Will never happen because "UTF-8" is a constant
        }

        return stringToEncode; // Will never be reached since sneakyThrows() will throw
    }

    static String getLocale(HttpServletRequest request) {
        final Cookie[] emptyCookies = {};
        return of(ofNullable(request.getCookies()).orElse(emptyCookies)).stream()
            .filter(c -> "locale".equals(c.getName()))
            .map(Cookie::getValue)
            .findFirst()
            .orElse("en_GB");
    }

    static boolean nullOrEmpty(String s) {
        return s == null || s.isBlank();
    }

    // Trick to make compiler stop complaining about unhandled checked exceptions
    // (which is truly annoying clutter where the exception quite possible can't happen)
    @SuppressWarnings("unchecked")
    public static <E extends Throwable> void sneakyThrows(Throwable e) throws E {
        throw (E) e;
    }
}
