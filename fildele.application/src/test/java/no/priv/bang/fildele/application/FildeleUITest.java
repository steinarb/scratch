package no.priv.bang.fildele.application;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.vaadin.server.VaadinRequest;

public class FildeleUITest {

    @Test
    public void testInit() {
        FildeleUI ui = new FildeleUI();
        assertNotNull(ui.getPage());
        VaadinRequest request = mock(VaadinRequest.class);
        when(request.getPathInfo()).thenReturn("/jad/subdir");
        ui.setRepoMap(createMockRepoMap());
        ui.init(request);
    }

    @Test
    public void testInitNoSubdirEndsWithSlash() {
        FildeleUI ui = new FildeleUI();
        assertNotNull(ui.getPage());
        VaadinRequest request = mock(VaadinRequest.class);
        when(request.getPathInfo()).thenReturn("/jad/");
        ui.setRepoMap(createMockRepoMap());
        ui.init(request);
    }

    @Test
    public void testInitNoSubdir() {
        FildeleUI ui = new FildeleUI();
        assertNotNull(ui.getPage());
        VaadinRequest request = mock(VaadinRequest.class);
        when(request.getPathInfo()).thenReturn("/jad");
        ui.setRepoMap(createMockRepoMap());
        ui.init(request);
    }

    private Map<String, Path> createMockRepoMap() {
        HashMap<String, Path> repoMap = new HashMap<String, Path>();
        repoMap.put("jad", Paths.get("/var/www-fildele/jad"));
        return repoMap;
    }

}
