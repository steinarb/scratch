package no.priv.bang.oldalbum.web.frontend;

import static org.osgi.service.http.whiteboard.HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME;

import javax.servlet.Servlet;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardContextSelect;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardServletName;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardServletPattern;
import no.priv.bang.servlet.jersey.JerseyServlet;

@Component(service=Servlet.class, immediate=true)
@HttpWhiteboardContextSelect("(" + HTTP_WHITEBOARD_CONTEXT_NAME + "=oldalbum)")
@HttpWhiteboardServletName("oldalbumpages")
@HttpWhiteboardServletPattern("/pages/*")
public class PagesServlet extends JerseyServlet {

    private static final long serialVersionUID = -4441841049428866032L;

    public void activate() {
        // Called when component is activated
    }

}
