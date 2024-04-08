package no.priv.bang.karaf.derby.embedded;

import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;
import javax.sql.XADataSource;

import org.apache.derby.jdbc.EmbeddedConnectionPoolDataSource;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.apache.derby.jdbc.EmbeddedDriver;
import org.apache.derby.jdbc.EmbeddedXADataSource;
import org.ops4j.pax.jdbc.common.BeanConfig;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.jdbc.DataSourceFactory;
import static org.osgi.service.jdbc.DataSourceFactory.*;

@Component(property = {OSGI_JDBC_DRIVER_NAME + ":String=derby", OSGI_JDBC_DRIVER_CLASS + ":String=org.apache.derby.jdbc.EmbeddedDriver"}, immediate = true)
public class EmbeddedDerbyDataSourceFactory implements DataSourceFactory {

    static final String JDBC_DERBY = "jdbc:derby:";
    static final Object CREATE_DATABASE = "createDatabase";

    @Override
    public DataSource createDataSource(Properties props) throws SQLException {
        var datasource = new EmbeddedDataSource();
        setProperties(datasource, props);
        return datasource;
    }

    private void setProperties(EmbeddedDataSource datasource, Properties props) {
        var properties = (Properties) props.clone();
        String databaseName = (String) properties.remove(DataSourceFactory.JDBC_DATABASE_NAME);
        if (databaseName != null) {
            datasource.setDatabaseName(databaseName);
        }

        String createDatabase = (String) properties.remove(CREATE_DATABASE);
        datasource.setCreateDatabase(createDatabase);

        String password = (String) properties.remove(DataSourceFactory.JDBC_PASSWORD);
        datasource.setPassword(password);

        String user = (String) properties.remove(DataSourceFactory.JDBC_USER);
        datasource.setUser(user);

        String url = (String) properties.remove(DataSourceFactory.JDBC_URL);
        applyUrl(datasource, url);

        if (!properties.isEmpty()) {
            BeanConfig.configure(datasource, properties);
        }
    }

    private void applyUrl(EmbeddedDataSource datasource, String url) {
        if (url == null) {
            return;
        }

        if (!url.startsWith(JDBC_DERBY)) {
            throw new IllegalArgumentException("The supplied URL is no derby url: " + url);
        }

        var suburl = url.substring(JDBC_DERBY.length());
        var parts = suburl.split(";");
        var database = parts[0];
        if (database != null) {
            datasource.setDatabaseName(database);
        }

        if (parts.length > 1) {
            var options = parts[1];
            if (options.length() > 0) {
                datasource.setConnectionAttributes(options);
            }
        }
    }

    @Override
    public ConnectionPoolDataSource createConnectionPoolDataSource(Properties properties) throws SQLException {
        var datasource = new EmbeddedConnectionPoolDataSource();
        setProperties(datasource, properties);
        return datasource;
    }

    @Override
    public XADataSource createXADataSource(Properties properties) throws SQLException {
        var datasource = new EmbeddedXADataSource();
        setProperties(datasource, properties);
        return datasource;
    }

    @Override
    public Driver createDriver(Properties props) throws SQLException {
        return new EmbeddedDriver();
    }

}
