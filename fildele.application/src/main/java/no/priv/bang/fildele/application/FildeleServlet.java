package no.priv.bang.fildele.application;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import com.vaadin.server.DefaultUIProvider;
import com.vaadin.server.ServiceException;
import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.SessionInitListener;
import com.vaadin.server.UIProvider;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinServletService;
import com.vaadin.server.VaadinSession;

public class FildeleServlet extends VaadinServlet {
    private static final long serialVersionUID = -2779674591943641723L;
    private FildeleServletProvider fildeleServletProvider;

    public FildeleServlet(FildeleServletProvider fildeleServletProvider) {
        this.fildeleServletProvider = fildeleServletProvider;
    }

    @Override
    protected void servletInitialized() throws ServletException {
        super.servletInitialized();
        addSessionInitListenerThatWillSetUIProviderOnSession();
    }

    private void addSessionInitListenerThatWillSetUIProviderOnSession() {
        VaadinServletService service = getService();
        service.addSessionInitListener(new SessionInitListener() {
                private static final long serialVersionUID = 131089958279902855L;

                @Override
                public void sessionInit(SessionInitEvent sessionInitEvent) throws ServiceException {
                    VaadinSession session = sessionInitEvent.getSession();
                    removeDefaultUIProvider(session);
                    session.addUIProvider(fildeleServletProvider);
                }

                private void removeDefaultUIProvider(VaadinSession session) {
                    List<UIProvider> uiProviders = new ArrayList<UIProvider>(session.getUIProviders());
                    for (UIProvider uiProvider : uiProviders) {
                        if (DefaultUIProvider.class.getCanonicalName().equals(uiProvider.getClass().getCanonicalName())) {
                            session.removeUIProvider(uiProvider);
                        }
                    }
                }
            });
    }

}
