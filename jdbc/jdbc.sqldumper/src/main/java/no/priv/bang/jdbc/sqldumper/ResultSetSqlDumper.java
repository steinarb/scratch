package no.priv.bang.jdbc.sqldumper;
/*
 * Copyright 2023-2026 Steinar Bang
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

import static java.lang.Character.toLowerCase;
import static java.lang.Character.toUpperCase;
import static java.util.Optional.ofNullable;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

/**
 * <p>A Java class containing methods for dumping a JDBC {@link ResultSet} in various ways:
 * <ul>
 * <li>to an {@link OutputStream} as an <a href="https://docs.liquibase.com/concepts/changelogs/sql-format.html">SQL formatted liquibase changeset</a> {@code #dumpResultSetAsSql(String, ResultSet, Writer)}</li>
 * <li>to a {@link Writer} as <a href="https://en.wikipedia.org/wiki/Comma-separated_values">CSV file</a> {@link #dumpResultSetAsCsv(ResultSet, Writer, String)}</li>
 * <li>to a {@link Writer} as a <a href="https://en.wikipedia.org/wiki/JSON">JSON array of objects</a> {@link #dumpResultSetAsJson(ResultSet, Writer)}</li>
 * </ul>
 *
 * This class also contains methods intended to be used when debugging an in-memory database in JUnit tests
 * (where it is hard to do normal queries to see what is in the database):
 * <ul>
 * <li>pretty printing a result set (iterating through the resultset, so can't use that resultset for anything else) {@link #prettyPrintResultSet(ResultSet)}</li>
 * <li>pretty printing the current resultset row (and be used when iterating the resultset for something else) {@link #prettyPrintResultSetRow(ResultSet)}</li>
 * <li>running an SQL query and outputting the pretty printed result (typically to do a quick select to see what's actually in an in-memory database) {@link #prettyPrintSqlQuery(DataSource, String)}</li>
 * </ul>
 */
public class ResultSetSqlDumper {

    private static final int ESTIMATED_NUMBER_OF_CHARACTERS_NEEDING_ESCAPING = 8;
    private static final String CSV_SEPARATOR = ",";

    /***
     * Create an instance of the result set dumper
     */
    public ResultSetSqlDumper() {
        // No-args constructor
    }

    /**
     * Traverse the JDBC {@link ResultSet} {@code
     * resultSetToGenerateSqlFor} and output an <a
     * href="https://www.liquibase.com/blog/liquibase-formatted-sql">SQL
     * formatted liquibase changeset</a> to {@code outputStream} with
     * a liquibase changeset id given by {@code changesetId}, on the
     * form "author:id".
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
     * <li>If you are using MSSQL (any version), Oracle prior to 23c or SQLite prior to 3.23.0 and have columns with boolean values in the ResultSet, then the generated SQL won't work for restoring the table, since boolean values are represented as <code>true</code> and <code>false</code> in the generated SQL insert statements.</li>
     * </ol>
     *
     * @param changesetId the id to use on the generated changeset
     * @param resultSetToGenerateSqlFor the JDBC {@link ResultSet} to generate output for
     * @param writer where the liquibase SQL formatted changeset will be written
     */
    public void dumpResultSetAsSql(String changesetId, ResultSet resultSetToGenerateSqlFor, Writer writer) {
        try(var bufferedwriter = new BufferedWriter(writer)) {
            bufferedwriter.write("--liquibase formatted sql");
            bufferedwriter.newLine();
            bufferedwriter.write("--changeset sb:saved_albumentries");
            bufferedwriter.newLine();
            var columnames = findColumnNames(resultSetToGenerateSqlFor);
            var columntypes = findColumntypes(resultSetToGenerateSqlFor);
            var tablename = findTableName(resultSetToGenerateSqlFor);
            if(resultSetToGenerateSqlFor.next()) {
                addInsertStatement(bufferedwriter, tablename, columnames);
                addValues(bufferedwriter, resultSetToGenerateSqlFor, columnames, columntypes);
                while(resultSetToGenerateSqlFor.next()) {
                    bufferedwriter.write(",");
                    bufferedwriter.newLine();
                    addValues(bufferedwriter, resultSetToGenerateSqlFor, columnames, columntypes);
                }
                bufferedwriter.write(";");
                bufferedwriter.newLine();
            }
        } catch (IOException | SQLException e) {
            throw new ResultsetSqlDumperException("Error dumping JDBC ResultSet as SQL insert statements", e);
        }
    }

    /**
     * Traverse the JDBC {@link ResultSet} {@code
     * resultSetToGenerateSqlFor} and output an <a
     * href="https://www.liquibase.com/blog/liquibase-formatted-sql">SQL
     * formatted liquibase changeset</a> to {@code outputStream} with
     * a liquibase changeset id given by {@code changesetId}, on the
     * form "author:id".
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
     *
     * @param changesetId the id to use on the generated changeset
     * @param resultSetToGenerateSqlFor the JDBC {@link ResultSet} to generate output for
     * @param outputstream where the liquibase SQL formatted changeset will be written
     * @throws IOException that Java insists can happen on {@code OutputStreamWriter#close()} but can never really happen since it would be caught in called code which turns it into a {@code ResultsetSqlDumperException}
     * @deprecated Use {@link #dumpResultSetAsSql(String,ResultSet,Writer)} instead
     */
    @Deprecated(since = "1.4.0", forRemoval = true)
    public void dumpResultSetAsSql(String changesetId, ResultSet resultSetToGenerateSqlFor, OutputStream outputstream) throws IOException {
        try(var writer = new OutputStreamWriter(outputstream, StandardCharsets.UTF_8)) {
            dumpResultSetAsSql(changesetId, resultSetToGenerateSqlFor, writer);
        }
    }

    /**
     * Traverse the JDBC {@link ResultSet} {@code
     * resultset} and output a <a
     * href="https://en.wikipedia.org/wiki/Comma-separated_values">CSV
     * file</a>.
     *
     * <em>Note</em>: there are no options to set how the CSV is generated.
     * The CSV is targeted to be parsed out of the box by RDBMSes:
     * <ul>
     * <li>There is no way to set the separator, it is always ","</li>
     * <li>Floating point numbers are always written US decimal comma, i.e. "."</li>
     * <li>nulls are represented as empty strings (i.e. "nothing" between two commas ",,"</li>
     * <li>Time stamps are written as unquoted <a href="https://en.wikipedia.org/wiki/ISO_8601">ISO 8601 formatted date times</a></li>
     * <li>Strings are quoted with double quotes</li>
     * </ul>
     *
     * <em>Warning</em>: Conversion of numerical values to strings is left to the JDBC driver (to
     * keep things simple). That means that if you are e.g. using the Oracle thin driver, or another
     * driver that respects the locale, and are in a locale that uses European decimal comma
     * you will need to set the JVM locale while calling this method.
     *
     * @param resultset the JDBC {@link ResultSet} to generate output for
     * @param writer where the CSV file will be written
     * @deprecated Use {@link #dumpResultSetAsCsv(ResultSet,Writer,String)} instead
     */
    public void dumpResultSetAsCsv(ResultSet resultset, Writer writer) {
        dumpResultSetAsCsv(resultset, writer, null);
    }

    /**
     * Traverse the JDBC {@link ResultSet} {@code
     * resultset} and output a <a
     * href="https://en.wikipedia.org/wiki/Comma-separated_values">CSV
     * file</a>.
     *
     * <em>Note</em>: there are no options to set how the CSV is generated.
     * The CSV is targeted to be parsed out of the box by RDBMSes:
     * <ul>
     * <li>There is no way to set the separator, it is always ","</li>
     * <li>Floating point numbers are always written US decimal comma, i.e. "."</li>
     * <li>nulls are represented as empty strings (i.e. "nothing" between two commas ",,"</li>
     * <li>Time stamps are written as unquoted <a href="https://en.wikipedia.org/wiki/ISO_8601">ISO 8601 formatted date times</a></li>
     * <li>Strings are quoted with double quotes</li>
     * </ul>
     *
     * <em>Warning</em>: Conversion of numerical values to strings is left to the JDBC driver (to
     * keep things simple). That means that if you are e.g. using the Oracle thin driver, or another
     * driver that respects the locale, and are in a locale that uses European decimal comma
     * you will need to set the JVM locale while calling this method.
     *
     * @param resultset the JDBC {@link ResultSet} to generate output for
     * @param writer where the CSV file will be written
     * @param stringNullPlaceHolder set a value to be output for null String values to satisfy liquibase CSV loads, with value null will output an empty column which works with RDBMS CSV imports
     */
    public void dumpResultSetAsCsv(ResultSet resultset, Writer writer, String stringNullPlaceHolder) {
        try (var bufferedWriter = new BufferedWriter(writer)) {
            var columnames = findColumnNames(resultset);
            var columntypes = findColumntypes(resultset);
            writeCsvHeaderLine(bufferedWriter, columnames);
            while(resultset.next()) {
                writeCsvLine(bufferedWriter, resultset, columnames, columntypes, stringNullPlaceHolder);
            }
        } catch (IOException | SQLException e) {
            throw new ResultsetSqlDumperException("Error dumping JDBC ResultSet as CSV file", e);
        }
    }

    /**
     * Traverse the JDBC {@link ResultSet} {@code resultset}
     * and output a <a href="https://en.wikipedia.org/wiki/JSON">JSON file</a>.
     *
     * Notes on the conversion:
     * <ul>
     * <li>Output is an array containing a JSON object for each row in the ResultSet</li>
     * <li>Snake case in column names are turned into camelCase property names</li>
     * <li>Floating point numbers are always written US decimal comma, i.e. "."</li>
     * <li>Properties are not omitted for null values</li>
     * </ul>
     *
     * <em>Warning</em>: Conversion of numerical values to strings is left to the JDBC driver (to
     * keep things simple). That means that if you are e.g. using the Oracle thin driver, or another
     * driver that respects the locale, and are in a locale that uses European decimal comma
     * you will need to set the JVM locale while calling this method.
     *
     * @param resultset the JDBC {@link ResultSet} to generate output for
     * @param writer where the JSON file will be written
     */
    public void dumpResultSetAsJson(ResultSet resultset, Writer writer) {
        try (var bufferedWriter = new BufferedWriter(writer)) {
            var columnames = findColumnNames(resultset);
            var columntypes = findColumntypes(resultset);
            bufferedWriter.write("[");
            if(resultset.next()) {
                writeJsonObject(bufferedWriter, resultset, columnames, columntypes);
            }
            while(resultset.next()) {
                bufferedWriter.write(",");
                bufferedWriter.newLine();
                bufferedWriter.write(" ");
                writeJsonObject(bufferedWriter, resultset, columnames, columntypes);
            }
            bufferedWriter.write("]");
            bufferedWriter.newLine();
        } catch (IOException | SQLException e) {
            throw new ResultsetSqlDumperException("Error dumping JDBC ResultSet as JSON file", e);
        }
    }

    /**
     * Return a pretty-printed version of an JDBC {@link ResultSet} as a string.
     *
     * Intended as a debugging tool for dumping result sets between database
     * operations in unit tests.
     *
     * @param resultset the JDBC {@link ResultSet} to generate output for
     * @return a {@link String} containing a pretty-printed result set
     */
    public String prettyPrintResultSet(ResultSet resultset) {
        var stringbuilder = new StringBuilder();
        try {
            var columntypes = findColumntypes(resultset);
            var columnames = findColumnNames(resultset);
            while(resultset.next()) {
                appendCurrentResultSetRowInPrettyPrintedForm(resultset, stringbuilder, columntypes, columnames);
                stringbuilder.append(System.lineSeparator());
            }
        } catch (SQLException e) {
            throw new ResultsetSqlDumperException("Error pretty printing JDBC ResultSet", e);
        }

        return stringbuilder.toString();
    }

    /**
     * Return a pretty-printed version of the current row in an JDBC {@link ResultSet} as a string.
     *
     * Intended as a debugging tool for dumping parts of result sets to logs and console during database
     * operations in unit tests.
     *
     * Note that {@link ResultSet#next()} must be called before calling this method.
     *
     * Note also that calling this method will not advance the current row of the resultset.
     * I.e. this method can be called before the resultset is used to see what the resultset contains.
     *
     * @param resultset the JDBC {@link ResultSet} to generate output for a row in
     * @return a {@link String} containing a pretty-printed result set row
     */
    public String prettyPrintResultSetRow(ResultSet resultset) {
        var stringbuilder = new StringBuilder();
        try {
            var columntypes = findColumntypes(resultset);
            var columnames = findColumnNames(resultset);
            appendCurrentResultSetRowInPrettyPrintedForm(resultset, stringbuilder, columntypes, columnames);
        } catch (SQLException e) {
            throw new ResultsetSqlDumperException("Error pretty printing JDBC ResultSet row", e);
        }

        return stringbuilder.toString();
    }

    /**
     * Return a pretty-printed version of the result of an SQL query
     *
     * Intended as a debugging tool for dumping result sets between database
     * operations in unit tests.
     *
     * This method solves solves the problem of doing a quick SQL select on
     * tables in an in-memory H2 or derby database in tests.
     *
     * {@snippet :
     * @Test
     * void testAddLikes() throws Exception {
     *     var sqldumper = new ResultSetSqlDumper();
     *     var properties = new Properties();
     *     properties.setProperty(DataSourceFactory.JDBC_URL, "jdbc:derby:memory:ratatoskr;create=true");
     *     var datasource = derbyDataSourceFactory.createDataSource(properties);
     *     System.err.println("likes before: " + sqldumper.prettyPrintSqlQuery(datasource, "select * from likes"));
     *     ...
     * }
     * }
     * @param datasource the JDBC {@link DataSource} to provide JDBC connection to send SQL query to
     * @param sql the SQL query to pretty print the results of
     * @return a {@link String} containing a pretty-printed result of an SQL query
     */
    public String prettyPrintSqlQuery(DataSource datasource, String sql) {
        try(var connection = datasource.getConnection()) {
            try(var statement = connection.createStatement()) {
                try(var resultset = statement.executeQuery(sql)) {
                    return prettyPrintResultSet(resultset);
                }
            }
        } catch (SQLException e) {
            throw new ResultsetSqlDumperException("Error pretty printing SQL query result", e);
        }
    }

    List<String> findColumnNames(ResultSet resultset) throws SQLException {
        var metadata = resultset.getMetaData();
        var columnames = new ArrayList<String>();
        for (var i = 1; i<=metadata.getColumnCount(); ++i) {
            columnames.add(metadata.getColumnName(i));
        }

        return columnames;
    }

    Map<String, Integer> findColumntypes(ResultSet resultset) throws SQLException {
        var columtypes = new HashMap<String, Integer>();
        var metadata = resultset.getMetaData();
        for (var i = 1; i<=metadata.getColumnCount(); ++i) {
            columtypes.put(metadata.getColumnName(i), metadata.getColumnType(i));
        }

        return columtypes;
    }

    String findTableName(ResultSet resultset) throws SQLException {
        var metadata = resultset.getMetaData();
        return metadata.getTableName(1);
    }

    static String columnameToJsonPropertyName(String columnname) {
        var jsonPropertyName = new StringBuilder();
        var isUpper = false;
        for (var c : columnname.toCharArray()) {
            if (c != '_') {
                jsonPropertyName.append(isUpper ? toUpperCase(c) : toLowerCase(c));
                isUpper = false;
            } else {
                isUpper = true;
            }
        }

        return jsonPropertyName.toString();
    }

    static String escapeJsonString(String stringToBeEscaped) {
        var stringbuilder = new StringBuilder(stringToBeEscaped.length() + ESTIMATED_NUMBER_OF_CHARACTERS_NEEDING_ESCAPING);

        for (var ch : stringToBeEscaped.toCharArray()) {
            switch (ch) {
                case '"'  -> stringbuilder.append("\\\"");
                case '\\' -> stringbuilder.append("\\\\");
                case '\n' -> stringbuilder.append("\\n");
                default   -> stringbuilder.append(ch);
            }
        }

        return stringbuilder.toString();
    }

    private void writeCsvHeaderLine(BufferedWriter writer, List<String> columnames) throws IOException {
        writer.write(String.join(CSV_SEPARATOR, columnames));
        writer.newLine();
    }

    private void writeCsvLine(BufferedWriter writer, ResultSet resultset, List<String> columnames, Map<String, Integer> columntypes, String stringNullPlaceHolder) throws IOException, SQLException {
        for (var i = 0; i< columnames.size()-1; ++i) {
            writeCsvValue(writer, resultset, columnames.get(i), columntypes, stringNullPlaceHolder);
            writer.write(CSV_SEPARATOR);
        }

        writeCsvValue(writer, resultset, columnames.getLast(), columntypes, stringNullPlaceHolder);
        writer.newLine();
    }

    private void writeCsvValue(BufferedWriter writer, ResultSet resultset, String columname, Map<String, Integer> columntypes, String stringNullPlaceHolder) throws IOException, SQLException {
        switch (columntypes.get(columname)) {
            case Types.BIT, Types.BOOLEAN -> writer.write(csvBooleanOrNull(resultset, columname));
            case Types.VARCHAR, Types.NVARCHAR -> writer.write(csvQuotedStringOrNull(resultset, columname, stringNullPlaceHolder));
            default -> writer.write(csvValueOrNull(resultset, columname));
        }
    }

    private String csvBooleanOrNull(ResultSet resultset, String columname) throws SQLException {
        var booleanVal = resultset.getBoolean(columname);
        if (resultset.wasNull()) {
            return ""; // null representation of CSV is empty string
        }

        return booleanVal ? "1" : "0";
    }

    String csvQuotedStringOrNull(ResultSet resultset, String columname, String stringNullPlaceHolder) throws SQLException {
        var stringVal = resultset.getString(columname);
        if (resultset.wasNull()) {
            return ofNullable(stringNullPlaceHolder).orElse("");
        }

        return "\"" + stringVal.replace("\"", "\"\"") + "\"";
    }

    private String csvValueOrNull(ResultSet resultset, String columname) throws SQLException {
        var stringVal = resultset.getString(columname);
        if (resultset.wasNull()) {
            return ""; // null representation of CSV is empty string
        }

        return stringVal;
    }

    private void writeJsonObject(BufferedWriter writer, ResultSet resultset, List<String> columnames, Map<String, Integer> columntypes) throws IOException, SQLException {
        writer.write("{ ");
        for (var i = 0; i< columnames.size()-1; ++i) {
            writeJsonProperty(writer, resultset, columnames.get(i), columntypes);
            writer.write(", ");
        }

        writeJsonProperty(writer, resultset, columnames.getLast(), columntypes);
        writer.write(" }");
    }

    private void writeJsonProperty(BufferedWriter writer, ResultSet resultset, String columnname, Map<String, Integer> columntypes) throws IOException, SQLException {
        writer.write("\"");
        writer.write(columnameToJsonPropertyName(columnname));
        writer.write("\": ");
        writeJsonValue(writer, resultset, columnname, columntypes.get(columnname));
    }

    private void writeJsonValue(BufferedWriter writer, ResultSet resultset, String columnname, Integer columntype) throws IOException, SQLException {
        switch (columntype) {
            case Types.VARCHAR, Types.NVARCHAR, Types.TIMESTAMP, Types.TIMESTAMP_WITH_TIMEZONE-> writer.write(jsonQuotedStringValueOrNull(resultset, columnname));
            default -> writer.write(jsonUnquotedValue(resultset, columnname));
        }
    }

    private String jsonQuotedStringValueOrNull(ResultSet resultset, String columnname) throws SQLException {
        var value = resultset.getString(columnname);
        if (resultset.wasNull()) {
            return "null";
        }

        return "\"" + escapeJsonString(value) + "\"";
    }

    private String jsonUnquotedValue(ResultSet resultset, String columnname) throws SQLException {
        var value = resultset.getString(columnname);
        if (resultset.wasNull()) {
            return "null";
        }

        return value;
    }

    private void appendCurrentResultSetRowInPrettyPrintedForm(ResultSet resultset, StringBuilder stringbuilder, Map<String, Integer> columntypes, List<String> columnames) throws SQLException {
        stringbuilder.append("[ ");
        for(var columname : columnames) {
            stringbuilder.append(columname);
            stringbuilder.append("=");
            var stringValue = resultset.getString(columname);
            if (columntypes.get(columname) == Types.VARCHAR) {
                doubleQuoteStringButNotNull(stringbuilder, stringValue);
            } else {
                stringbuilder.append(stringValue);
            }

            stringbuilder.append(" ");
        }

        stringbuilder.append("]");
    }

    private void addInsertStatement(BufferedWriter writer, String tablename, List<String> columnames) throws IOException {
        writer.write("insert into ");
        writer.write(tablename);
        writer.write(" (");
        writer.write(String.join(", ", columnames));
        writer.write(") values");
        writer.newLine();
    }

    private void addValues(BufferedWriter writer, ResultSet resultset, List<String> columnames, Map<String, Integer> columntypes) throws SQLException, IOException {
        var values = new ArrayList<String>();
        for(var columname : columnames) {
            var stringValue = resultset.getString(columname);
            if (columntypes.get(columname) == Types.VARCHAR) {
                values.add(quoteStringButNotNull(stringValue));
            } else if (columntypes.get(columname) == Types.TIMESTAMP && !resultset.wasNull()) {
                values.add(String.format("'%s'", stringValue));
            } else {
                values.add(stringValue);
            }
        }

        writer.write(" (");
        writer.write(String.join(", ", values));
        writer.write(")");
    }

    private String quoteStringButNotNull(String string) {
        if (string != null) {
            var builder = new StringBuilder(string.length() + 10);
            builder.append("'");
            builder.append(string.replace("'", "''"));
            builder.append("'");
            return builder.toString();
        }

        return string;
    }

    private void doubleQuoteStringButNotNull(StringBuilder builder, String string) {
        if (string != null) {
            builder.append("\"");
            builder.append(string.replace("'", "''"));
            builder.append("\"");
        } else {
            builder.append(string);
        }
    }

}
