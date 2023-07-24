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

/**
 * <p>A Java class for dumping a JDBC
 * {@link ResultSet} to an {@link OutputStream} as an
 * <a href="https://docs.liquibase.com/concepts/changelogs/sql-format.html">SQL
 * formatted liquibase changeset</a>.
 *
 * <p>Sample usage:
 * <pre>
 *     private void dumpAlbumEntriesAsLiquibaseSql(DataSource oldalbumDatasource, OutputStream outputstream) throws SQLException, IOException {
 *         var sqldumper = new ResultSetSqlDumper();
 *         var changesetId = "sb:album_paths";
 *         var sql = "select * from albumentries";
 *         try(var connection = oldalbumDatasource.getConnection()) {
 *             try(var statement = connection.createStatement()) {
 *                 try(var resultset = statement.executeQuery(sql)) {
 *                     sqldumper.dumpResultSetAsSql(changesetId, resultset, outputstream);
 *                 }
 *             }
 *         }
 *     }
 * </pre>
 *
 * <p>Limitations:
 * <ol>
 * <li>The select that creates the {@link ResultSet} must be from a single table. I.e. the select cannot be a join between table. The SQL file will be generated but won't be importable</li>
 * <li>The columns of the {@link ResultSet} can't be of complex types like structs or arrays, only numbers, strings, booleans and dates will work</li>
 * <li>If an autoincremented key is part of the SQL dump, the counter won't be set right after the import, and there is no portable way of resetting the counter (different RDBMSes does it different ways)</li>
 * </ol>
 */
public class ResultSetSqlDumper {

    /**
     * Traverse the JDBC {@link ResultSet} {@code
     * resultSetToGenerateSqlFor} and output an <a
     * href="https://www.liquibase.com/blog/liquibase-formatted-sql">SQL
     * formatted liquibase changeset</a> to {@code outputStream} with
     * a liquibase changeset id given by {@code changesetId}, on the
     * form "author:id".
     *
     * @param changesetId the id to use on the generated changeset
     * @param resultSetToGenerateSqlFor the JDBC {@link ResultSet} to generate output for
     * @param outputstream where the liquibase SQL formatted changeset will be written
     * @throws IOException when there is an error writing the changeset as SQL
     * @throws SQLException when there is an error accessing the {@link ResultSet}
     */
    public void dumpResultSetAsSql(String changesetId, ResultSet resultSetToGenerateSqlFor, OutputStream outputstream) throws IOException, SQLException {
        try(var writer = new OutputStreamWriter(outputstream, "UTF-8")) {
            writer.write("--liquibase formatted sql\n");
            writer.write("--changeset sb:saved_albumentries\n");
            var columnames = findColumnNames(resultSetToGenerateSqlFor);
            var columntypes = findColumntypes(resultSetToGenerateSqlFor);
            var tablename = findTableName(resultSetToGenerateSqlFor);
            while(resultSetToGenerateSqlFor.next()) {
                addInsertStatement(writer, tablename, columnames);
                addValues(writer, resultSetToGenerateSqlFor, columnames, columntypes);
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
