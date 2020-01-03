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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.PrintWriter;
import java.sql.Connection;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;

class DataSourceProxyTest {

    @Test
    void testWrapDataSource() throws Exception {
        DataSource wrappedDataSource = mock(DataSource.class);
        PrintWriter logwriter = mock(PrintWriter.class);
        when(wrappedDataSource.getLogWriter()).thenReturn(logwriter);
        when(wrappedDataSource.getLoginTimeout()).thenReturn(42);
        Logger parentlogger = mock(Logger.class);
        when(wrappedDataSource.getParentLogger()).thenReturn(parentlogger);
        when(wrappedDataSource.isWrapperFor(any())).thenReturn(true);
        DataSource other = mock(DataSource.class);
        when(wrappedDataSource.unwrap(any())).thenReturn(other);
        Connection connection = mock(Connection.class);
        when(wrappedDataSource.getConnection()).thenReturn(connection);
        when(wrappedDataSource.getConnection(anyString(), anyString())).thenReturn(connection);

        DataSourceProxy proxy = new DataSourceProxy();
        proxy.setWrappedDataSource(wrappedDataSource);

        proxy.setLogWriter(logwriter);
        verify(wrappedDataSource, times(1)).setLogWriter(any());
        assertEquals(logwriter, proxy.getLogWriter());
        proxy.setLoginTimeout(42);
        verify(wrappedDataSource, times(1)).setLoginTimeout(anyInt());
        assertEquals(42, proxy.getLoginTimeout());
        assertEquals(parentlogger, proxy.getParentLogger());
        assertTrue(proxy.isWrapperFor(getClass()));
        assertEquals(other, proxy.unwrap(DataSource.class));
        assertEquals(connection, proxy.getConnection());
        assertEquals(connection, proxy.getConnection("karaf", "karaf"));
    }

}
