package no.priv.bang.fildele.application;

import javax.inject.Provider;
import javax.servlet.Servlet;

import org.ops4j.pax.web.extender.whiteboard.ExtenderConstants;

import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.server.UIProvider;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;

import no.steria.osgi.jsr330activator.ServiceProperties;
import no.steria.osgi.jsr330activator.ServiceProperty;


@ServiceProperties({
    @ServiceProperty( name = ExtenderConstants.PROPERTY_URL_PATTERNS, values = {"/fildele/*", "/VAADIN/*"}),
    @ServiceProperty( name = ExtenderConstants.PROPERTY_SERVLET_NAMES, value = "fildele")})
public class FildeleServletProvider extends UIProvider implements Provider<Servlet> {
    private static final long serialVersionUID = 8576709364797355112L;
    VaadinServlet servlet;

    @Override
    public Class<? extends UI> getUIClass(UIClassSelectionEvent event) {
        return FildeleUI.class;
    }

    @Override
    public Servlet get() {
        if (servlet == null) {
            servlet = new FildeleServlet(this);
        }

        return servlet;
    }

}
