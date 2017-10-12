package no.priv.bang.fildele.tests.mocks;

import java.util.Collections;
import java.util.Enumeration;

import javax.servlet.ServletContext;

public class MockServletConfig extends MockServletConfigBase {

    private MockServletContext servletContext;

    public MockServletConfig(MockServletContext context) {
        this.servletContext = context;
    }

    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        return Collections.enumeration(Collections.<String>emptyList());
    }

}
