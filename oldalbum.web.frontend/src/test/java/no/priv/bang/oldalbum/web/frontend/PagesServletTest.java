package no.priv.bang.oldalbum.web.frontend;

import static org.assertj.core.api.Assertions.assertThat;
import static org.glassfish.jersey.server.ServerProperties.PROVIDER_PACKAGES;
import javax.servlet.ServletConfig;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.junit.jupiter.api.Test;

import com.mockrunner.mock.web.MockHttpServletRequest;
import com.mockrunner.mock.web.MockHttpServletResponse;
import com.mockrunner.mock.web.MockHttpSession;
import com.mockrunner.mock.web.MockServletConfig;
import com.mockrunner.mock.web.MockServletContext;

import no.priv.bang.oldalbum.testutilities.ShiroTestBase;

class PagesServletTest extends ShiroTestBase {

    @Test
    void testGetSettingsPage() throws Exception {
        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration();
        var request = buildGetUrl("/settings");
        var response = new MockHttpServletResponse();

        createSubjectAndBindItToThread();

        // Do the request
        servlet.service(request, response);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    @Test
    void testSubmitSettingsForm() throws Exception {
        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration();
        var request = buildPostUrl("/settings");
        var body = UriBuilder.fromUri("http://localhost:8181/oldalbum")
            .queryParam("originalRequestUri", "http://localhost:8181/oldalbum/moto/vfr96/acirc1")
            .queryParam("locale", "en_GB")
            .build().getQuery();
        request.setBodyContent(body);
        var response = new MockHttpServletResponse();

        createSubjectAndBindItToThread();

        // Do the request
        servlet.service(request, response);

        assertThat(response.getStatusCode()).isEqualTo(302);
        assertThat(response.getHeader("Set-Cookie")).isNotEmpty();
    }

    private MockHttpServletRequest buildGetUrl(String resource) {
        return buildRequest(resource)
            .setMethod("GET");
    }

    private MockHttpServletRequest buildPostUrl(String resource) {
        var contenttype = MediaType.APPLICATION_FORM_URLENCODED;
        return buildRequest(resource)
            .setMethod("POST")
            .setContentType(contenttype)
            .addHeader("Content-Type", contenttype);
    }

    private MockHttpServletRequest buildRequest(String resource) {
        var session = new MockHttpSession();
        var contextPath = "/oldalbum";
        var servletPath = "/pages";
        var requestUri = contextPath + servletPath + resource;
        return new MockHttpServletRequest()
            .setProtocol("HTTP/1.1")
            .setRequestURL("http://localhost:8181" + requestUri)
            .setRequestURI(requestUri)
            .setPathInfo(resource)
            .setContextPath(contextPath)
            .setServletPath(servletPath)
            .setSession(session);
    }

    private PagesServlet simulateDSComponentActivationAndWebWhiteboardConfiguration() throws Exception {
        var servlet = new PagesServlet();
        servlet.activate();
        var config = createServletConfigWithApplicationAndPackagenameForJerseyResources();
        servlet.init(config);
        return servlet;
    }

    private ServletConfig createServletConfigWithApplicationAndPackagenameForJerseyResources() {
        var servletContext = new MockServletContext();
        servletContext.setContextPath("/oldalbum");
        servletContext.setServletContextName("oldalbum");
        var config = new MockServletConfig();
        config.setServletContext(servletContext);
        config.setInitParameter(PROVIDER_PACKAGES, "no.priv.bang.oldalbum.web.frontend.resources");
        return config;
    }

}
