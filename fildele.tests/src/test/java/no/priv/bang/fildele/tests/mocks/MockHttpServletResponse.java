package no.priv.bang.fildele.tests.mocks;

public class MockHttpServletResponse extends MockHttpServletResponseBase {

    private int statusCode = 200;
    private String statusMessage = "";

    @Override
    public void setStatus(int sc) {
        this.statusCode = sc;
    }

    @Override
    public void setStatus(int sc, String sm) {
        this.statusCode = sc;
        this.statusMessage = sm;
    }

    @Override
    public int getStatus() {
        return statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

}
