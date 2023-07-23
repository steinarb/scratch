package no.priv.bang.jdbc.sqldumper;
/*
 * Copyright 2023 Steinar Bang
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

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultSetSqlDumper {

    public void dumpResultSetAsSql(String changesetId, ResultSet resultset, OutputStream outputstream) throws IOException, SQLException {
        try(var writer = new OutputStreamWriter(outputstream, "UTF-8")) {
            writer.write("--liquibase formatted sql\n");
            writer.write("--changeset sb:saved_albumentries\n");
            var columnames = findColumnNames(resultset);
            var columntypes = findColumntypes(resultset);
            var tablename = findTableName(resultset);
            while(resultset.next()) {
                addInsertStatement(writer, tablename, columnames);
                addValues(writer, resultset, columnames, columntypes);
            }
        }
    }

    List<String> findColumnNames(ResultSet resultset) throws SQLException {
        var metadata = resultset.getMetaData();
        var columnames = new ArrayList<String>();
        for (int i = 1; i<=metadata.getColumnCount(); ++i) {
            columnames.add(metadata.getColumnName(i));
        }

        return columnames;
    }

    public Map<String, Integer> findColumntypes(ResultSet resultset) throws SQLException {
        var columtypes = new HashMap<String, Integer>();
        var metadata = resultset.getMetaData();
        for (int i = 1; i<=metadata.getColumnCount(); ++i) {
            columtypes.put(metadata.getColumnName(i), metadata.getColumnType(i));
        }

        return columtypes;
    }

    public String findTableName(ResultSet resultset) throws SQLException {
        var metadata = resultset.getMetaData();
        return metadata.getTableName(1);
    }

    private void addInsertStatement(OutputStreamWriter writer, String tablename, List<String> columnames) throws IOException {
        writer.write("insert into ");
        writer.write(tablename);
        writer.write(" (");
        writer.write(String.join(", ", columnames));
        writer.write(") values (");
    }

    private void addValues(OutputStreamWriter writer, ResultSet resultset, List<String> columnames, Map<String, Integer> columntypes) throws SQLException, IOException {
        var values = new ArrayList<String>();
        for(String columname : columnames) {
            var stringValue = resultset.getString(columname);
            if (columntypes.get(columname) == Types.VARCHAR) {
                values.add(quoteStringButNotNull(stringValue));
            } else if (columntypes.get(columname) == Types.TIMESTAMP && !resultset.wasNull()) {
                values.add(String.format("'%s'", stringValue));
            } else {
                values.add(stringValue);
            }
        }

        writer.write(String.join(", ", values));
        writer.write(");\n");
    }

    private String quoteStringButNotNull(String string) {
        if (string != null) {
            StringBuilder builder = new StringBuilder(string.length() + 10);
            builder.append("'");
            builder.append(string.replace("'", "''"));
            builder.append("'");
            return builder.toString();
        }
        return string;
    }

}
