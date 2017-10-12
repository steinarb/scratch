package no.priv.bang.fildele.tests.mocks;

import java.util.Collections;
import java.util.Enumeration;

public class MockServletContext extends MockServletContextBase {

    @Override
    public Enumeration<String> getInitParameterNames() {
        return Collections.enumeration(Collections.<String>emptyList());
    }

}
