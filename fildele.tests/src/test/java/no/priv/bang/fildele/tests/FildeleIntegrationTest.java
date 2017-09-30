package no.priv.bang.fildele.tests;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.ops4j.pax.exam.CoreOptions.*;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.*;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.URL;

import javax.inject.Inject;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.karaf.options.LogLevelOption.LogLevel;
import org.ops4j.pax.exam.options.MavenArtifactUrlReference;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class FildeleIntegrationTest {
    public static final String RMI_SERVER_PORT = "44446";
    public static final String RMI_REG_PORT = "1101";

    @Inject
    private Servlet servlet;

    @Configuration
    public Option[] config() {
        final String jmxPort = freePortAsString();
        final String httpPort = freePortAsString();
        final String httpsPort = freePortAsString();
        final MavenArtifactUrlReference karafUrl = maven().groupId("org.apache.karaf").artifactId("apache-karaf-minimal").type("zip").versionAsInProject();
        final MavenArtifactUrlReference mockitoFeatureRepo = maven().groupId("no.priv.bang.fildele").artifactId("karaf.mockito").versionAsInProject().type("xml").classifier("features");
        final MavenArtifactUrlReference fildeleFeatureRepo = maven().groupId("no.priv.bang.fildele").artifactId("fildele.application").versionAsInProject().type("xml").classifier("features");
        return options(
            karafDistributionConfiguration().frameworkUrl(karafUrl).unpackDirectory(new File("target/exam")).useDeployFolder(false).runEmbedded(true),
            configureConsole().ignoreLocalConsole().ignoreRemoteShell(),
            systemTimeout(120000),
            keepRuntimeFolder(),
            logLevel(LogLevel.DEBUG),
            editConfigurationFilePut("etc/org.apache.karaf.management.cfg", "rmiRegistryPort", RMI_REG_PORT),
            editConfigurationFilePut("etc/org.apache.karaf.management.cfg", "rmiServerPort", RMI_SERVER_PORT),
            editConfigurationFilePut("etc/org.ops4j.pax.web.cfg", "org.osgi.service.http.port", httpPort),
            editConfigurationFilePut("etc/org.ops4j.pax.web.cfg", "org.osgi.service.http.port.secure", httpsPort),
            replaceConfigurationFile("etc/org.ops4j.pax.logging.cfg", getConfigFile("/etc/org.ops4j.pax.logging.cfg")),
            frameworkProperty("felix.bootdelegation.implicit").value("false"),
            systemProperty("org.ops4j.pax.logging.DefaultSer‌​viceLog.level").value("DEBUG"),
            vmOptions("-Dtest-jmx-port=" + jmxPort),
            features(mockitoFeatureRepo, "mockito"),
            junitBundles(),
            features(fildeleFeatureRepo, "fildele"));
    }

    @Test
    public void testServlet() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        servlet.service(request, response);
    }

    public File getConfigFile(String path) {
        URL res = this.getClass().getResource(path);
        if (res == null) {
            throw new RuntimeException("Config resource " + path + " not found");
        }

        return new File(res.getFile());
    }

    static int freePort() {
        try (final ServerSocket serverSocket = new ServerSocket(0)) {
            serverSocket.setReuseAddress(true);
            final int port = serverSocket.getLocalPort();
            return port;
        } catch (final IOException e) {
            throw new IllegalStateException(e);
        }
    }

    static String freePortAsString() {
        return Integer.toString(freePort());
    }

}
