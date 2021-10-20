package no.priv.bang.karaf.liquibase.sample;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.ops4j.pax.jdbc.hook.PreHook;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import liquibase.Liquibase;
import liquibase.database.DatabaseConnection;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

@Component(immediate = true, property = "name=liquibasesampledb")
public class SampleLiquibase implements PreHook {

    @Activate
    public void activate() {
        // Called when component is activated
    }

    @Override
    public void prepare(DataSource datasource) throws SQLException {
        try(Connection connection = datasource.getConnection()) {
            createSchema(connection);
        } catch (LiquibaseException e) {
            throw new SQLException("Failed to create liquibase schema", e);
        }
    }

    public void createSchema(Connection connection) throws LiquibaseException {
        applyLiquibaseChangeLog(connection, "liquibasesample/changelog01.xml");
    }

    private void applyLiquibaseChangeLog(Connection connection, String changelogClasspathResource) throws LiquibaseException {
        Liquibase liquibase = createLiquibaseInstance(connection, changelogClasspathResource);
        liquibase.update("");
    }

    private Liquibase createLiquibaseInstance(Connection connection, String changelogClasspathResource) throws LiquibaseException {
        DatabaseConnection databaseConnection = new JdbcConnection(connection);
        ClassLoaderResourceAccessor classLoaderResourceAccessor = new ClassLoaderResourceAccessor(getClass().getClassLoader());
        return new Liquibase(changelogClasspathResource, classLoaderResourceAccessor, databaseConnection);
    }

}
