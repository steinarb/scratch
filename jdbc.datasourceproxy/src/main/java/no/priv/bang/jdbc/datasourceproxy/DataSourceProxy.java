package no.priv.bang.jdbc.datasourceproxy;
/*
 * Copyright 2019 Steinar Bang
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations
 * under the License.
 */

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

/**
 * This is class implements {@link DataSource} and is a proxy for
 * another {@link DataSource} that is set with the
 * {@link #setWrappedDataSource(DataSource)} method.
 *
 * All DataSource methods are delegated to the wrapped datasource.
 *
 * A usecase for this proxy is where you have a DataSource OSGi
 * service with a given service name, and wish to use that service
 * with a different service name.
 */
public class DataSourceProxy implements DataSource {
    private DataSource wrappedDataSource;

    /**
     * Sets the {@link DataSource} that all methods delegate to
     * @param wrappedDataSource is the DataSource instance implementing everything
     */
    public void setWrappedDataSource(DataSource wrappedDataSource) {
        this.wrappedDataSource = wrappedDataSource;
    }

    @Override
    public void setLogWriter(PrintWriter logwriter) throws SQLException {
        wrappedDataSource.setLogWriter(logwriter);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return wrappedDataSource.getLogWriter();
    }

    @Override
    public void setLoginTimeout(int timeoutInSeconds) throws SQLException {
        wrappedDataSource.setLoginTimeout(timeoutInSeconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return wrappedDataSource.getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return wrappedDataSource.getParentLogger();
    }

    @Override
    public boolean isWrapperFor(Class<?> clazz) throws SQLException {
        return wrappedDataSource.isWrapperFor(clazz);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return wrappedDataSource.unwrap(iface);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return wrappedDataSource.getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return wrappedDataSource.getConnection(username, password);
    }

}
