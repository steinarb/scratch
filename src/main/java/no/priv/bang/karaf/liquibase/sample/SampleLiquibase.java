package no.priv.bang.karaf.liquibase.sample;

import java.sql.Connection;

import liquibase.Liquibase;
import liquibase.database.DatabaseConnection;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

public class SampleLiquibase {

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
