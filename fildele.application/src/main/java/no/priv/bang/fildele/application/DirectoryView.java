package no.priv.bang.fildele.application;

import java.nio.file.Path;

import com.vaadin.navigator.View;
import com.vaadin.server.VaadinRequest;

public class DirectoryView implements View {
    private static final long serialVersionUID = 2430735031971344012L;
    private Path pathToResource;

    public DirectoryView(Path pathToResource, VaadinRequest request) {
        this.pathToResource = pathToResource;
    }

}
