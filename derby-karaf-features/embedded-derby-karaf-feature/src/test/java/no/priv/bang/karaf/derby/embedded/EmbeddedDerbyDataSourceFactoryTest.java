package no.priv.bang.karaf.derby.embedded;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Properties;

import org.apache.derby.jdbc.EmbeddedDataSource;
import org.junit.jupiter.api.Test;
import org.osgi.service.jdbc.DataSourceFactory;

class EmbeddedDerbyDataSourceFactoryTest {

    @Test
    void testCreateEmbeddedFileDataSource() throws Exception {
        DataSourceFactory dbfactory = new EmbeddedDerbyDataSourceFactory();
        var props = new Properties();
        props.setProperty(DataSourceFactory.JDBC_DATABASE_NAME, "target/test1");
        props.put(EmbeddedDerbyDataSourceFactory.CREATE_DATABASE, "create");
        var datasource = (EmbeddedDataSource) dbfactory.createDataSource(props);
        assertNotNull(datasource);
        assertEquals("target/test1", datasource.getDatabaseName());
        assertEquals("create", datasource.getCreateDatabase());
        assertNull(datasource.getPassword());
        assertNull(datasource.getUser());
        try(var connection = datasource.getConnection()) {
            var tablePersonExists = false;
            var metadata = connection.getMetaData();
            try(var tableResults = metadata.getTables(null, null, null, new String[] {"TABLE"})) {
                while(tableResults.next()) {
                    var tableName = tableResults.getString("TABLE_NAME");
                    if ("PERSON".equals(tableName)) {
                        tablePersonExists = true;
                    }
                }
            }

            if (!tablePersonExists) {
                var sql = "create table person (person_id int not null, name varchar(100))";
                try(var statement = connection.prepareStatement(sql)) {
                    statement.executeUpdate();
                }
            }
        }
    }

    @Test
    void testCreateEmbeddedFilePooledDataSource() throws Exception {
        DataSourceFactory dbfactory = new EmbeddedDerbyDataSourceFactory();
        var props = new Properties();
        props.setProperty(DataSourceFactory.JDBC_DATABASE_NAME, "target/test2");
        props.put(EmbeddedDerbyDataSourceFactory.CREATE_DATABASE, "create");
        var datasource = (EmbeddedDataSource) dbfactory.createConnectionPoolDataSource(props);
        assertNotNull(datasource);
        assertEquals("target/test2", datasource.getDatabaseName());
        assertEquals("create", datasource.getCreateDatabase());
        assertNull(datasource.getPassword());
        assertNull(datasource.getUser());
    }

    @Test
    void testCreateEmbeddedFileXADataSource() throws Exception {
        DataSourceFactory dbfactory = new EmbeddedDerbyDataSourceFactory();
        var props = new Properties();
        props.setProperty(DataSourceFactory.JDBC_DATABASE_NAME, "target/test3");
        props.put(EmbeddedDerbyDataSourceFactory.CREATE_DATABASE, "create");
        var datasource = (EmbeddedDataSource) dbfactory.createXADataSource(props);
        assertNotNull(datasource);
        assertEquals("target/test3", datasource.getDatabaseName());
        assertEquals("create", datasource.getCreateDatabase());
        assertNull(datasource.getPassword());
        assertNull(datasource.getUser());
    }

    @Test
    void testCreateEmbeddedFileDriver() throws Exception {
        DataSourceFactory dbfactory = new EmbeddedDerbyDataSourceFactory();
        var props = new Properties();
        var datasource = dbfactory.createDriver(props);
        assertNotNull(datasource);
    }

}
