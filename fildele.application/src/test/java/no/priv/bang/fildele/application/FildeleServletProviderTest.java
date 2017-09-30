package no.priv.bang.fildele.application;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import org.junit.Test;

import com.mockrunner.mock.web.MockHttpServletRequest;
import com.mockrunner.mock.web.MockHttpServletResponse;
import com.mockrunner.mock.web.WebMockObjectFactory;

public class FildeleServletProviderTest {

    @Test
    public void testGet() throws ServletException, IOException {
        WebMockObjectFactory mockFactory = new WebMockObjectFactory();
        String requestURI = "http://localhost:8181/fildele/jad";
        MockHttpServletRequest request = mockFactory.getMockRequest();
        request.setRequestURI(requestURI);
        request.setServletPath("/fildele");
        MockHttpServletResponse response = mockFactory.getMockResponse();

        // Make the provider create a servlet
        FildeleServletProvider servletProvider = new FildeleServletProvider();
        Servlet servlet = servletProvider.get();

        // Initialize the vaadin servlet
        ServletContext context = mock(ServletContext.class);
        Enumeration<String> emptyStringEnumerator = Collections.enumeration(Collections.<String>emptyList());
        when(context.getInitParameterNames()).thenReturn(emptyStringEnumerator);
        ServletConfig servletConfig = mock(ServletConfig.class);
        when(servletConfig.getServletContext()).thenReturn(context);
        when(servletConfig.getInitParameterNames()).thenReturn(emptyStringEnumerator);
        servlet.init(servletConfig);

        // Do a request
        servlet.service(request, response);

        // Verify that the servlet has produced results.
        String responseBody = response.getOutputStreamContent();
        assertEquals(2132, responseBody.length());
        assertEquals(200, response.getStatusCode());
        assertEquals("text/html; charset=utf-8", response.getContentType());
    }

}
