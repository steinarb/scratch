package no.priv.bang.fildele.application;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;

public class FildeleUI extends UI{
    private static final long serialVersionUID = 5922102075552629691L;
    private Map<String, Path> repoMap = new HashMap<String, Path>();

    @Override
    protected void init(VaadinRequest request) {
        Path pathInfo = Paths.get(request.getPathInfo());
        String rootDirectory = pathInfo.getName(0).toString();
        getPage().setTitle("fildele - " + rootDirectory);
        setNavigator(new Navigator(this, this));

        Path pathToRootDirectory = repoMap.get(rootDirectory);
        int numberOfPathElementsInPathInfo = getNumberOfPathElementsInPathInfo(pathInfo);
        Path pathToResource =
            numberOfPathElementsInPathInfo > 1
            ? pathToRootDirectory.resolve(pathInfo.subpath(1, numberOfPathElementsInPathInfo))
            : pathToRootDirectory;
        getNavigator().addView("", new DirectoryView(pathToResource, request));
    }

    private int getNumberOfPathElementsInPathInfo(Path pathInfo) {
        int numberOfPathElementsInPathInfo = 0;
        Iterator<Path> pathIterator = pathInfo.iterator();
        while (pathIterator.hasNext()) {
            ++numberOfPathElementsInPathInfo;
            pathIterator.next();
        }

        return numberOfPathElementsInPathInfo;
    }

    void setRepoMap(Map<String, Path> repoMap) {
        this.repoMap = repoMap;
    }

}
