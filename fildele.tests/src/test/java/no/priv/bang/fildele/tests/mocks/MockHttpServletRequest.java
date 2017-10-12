package no.priv.bang.fildele.tests.mocks;

public class MockHttpServletRequest extends MockHttpServletRequestBase {

    private String requestURI;
    private String servletPath;

    public MockHttpServletRequest(String pathInfo, String servletPath) {
        this.requestURI = pathInfo;
        this.servletPath = servletPath;
    }

    @Override
    public String getRequestURI() {
        return requestURI;
    }

    @Override
    public String getServletPath() {
        return servletPath;
    }

}
