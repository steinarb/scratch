/*
 * Copyright 2020-2026 Steinar Bang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
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
package no.priv.bang.oldalbum.backend;

import static java.lang.String.format;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.attribute.FileTime;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import javax.sql.DataSource;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.LogService;
import org.osgi.service.log.Logger;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.twelvemonkeys.imageio.metadata.CompoundDirectory;
import com.twelvemonkeys.imageio.metadata.Directory;
import com.twelvemonkeys.imageio.metadata.Entry;
import com.twelvemonkeys.imageio.metadata.exif.EXIF;
import com.twelvemonkeys.imageio.metadata.jpeg.JPEG;
import com.twelvemonkeys.imageio.metadata.jpeg.JPEGSegment;
import com.twelvemonkeys.imageio.metadata.tiff.IFD;
import com.twelvemonkeys.imageio.metadata.tiff.TIFF;
import com.twelvemonkeys.imageio.metadata.tiff.TIFFEntry;
import com.twelvemonkeys.imageio.metadata.tiff.TIFFReader;
import com.twelvemonkeys.imageio.metadata.tiff.TIFFWriter;
import com.twelvemonkeys.imageio.stream.ByteArrayImageInputStream;
import com.twelvemonkeys.imageio.util.ImageTypeSpecifiers;
import com.twelvemonkeys.lang.StringUtil;
import com.twelvemonkeys.xml.XMLSerializer;

import static com.twelvemonkeys.imageio.metadata.jpeg.JPEGSegmentUtil.*;

import no.priv.bang.jdbc.sqldumper.ResultSetSqlDumper;
import no.priv.bang.oldalbum.services.ImageIOService;
import no.priv.bang.oldalbum.services.OldAlbumException;
import no.priv.bang.oldalbum.services.OldAlbumService;
import no.priv.bang.oldalbum.services.bean.AlbumEntry;
import no.priv.bang.oldalbum.services.bean.BatchAddPicturesRequest;
import no.priv.bang.oldalbum.services.bean.ImageMetadata;
import no.priv.bang.oldalbum.services.bean.ImageMetadata.Builder;
import no.priv.bang.oldalbum.services.bean.LocaleBean;

@Component(immediate = true, property= { "defaultlocale=nb_NO" })
public class OldAlbumServiceProvider implements OldAlbumService {

    static final byte[] EXIF_ASCII_ENCODING = Arrays.copyOf("ASCII".getBytes(StandardCharsets.UTF_8), 8);
    static final int EXIF_DATETIME = 306;
    static final int EXIF_DESCRIPTION = 0x010e;
    static final int EXIF_EXIF = 34665;
    static final int EXIF_USER_COMMENT = 37510;

    private static final String DISPLAY_TEXT_RESOURCES = "i18n.Texts";
    private Logger logger;
    private DataSource datasource;
    private ImageIOService imageIOService;
    private HttpConnectionFactory connectionFactory;
    private Locale defaultLocale;

    @Reference
    public void setLogService(LogService logservice) {
        this.logger = logservice.getLogger(getClass());
    }

    @Reference(target = "(osgi.jndi.service.name=jdbc/oldalbum)")
    public void setDataSource(DataSource datasource) {
        this.datasource = datasource;
    }

    @Reference
    public void setImageIOService(ImageIOService service) {
        this.imageIOService = service;
    }

    @Activate
    public void activate(Map<String, Object> config) {
        defaultLocale = config.entrySet().stream()
            .filter(e -> "defaultlocale".equals(e.getKey()))
            .map(e -> Locale.forLanguageTag(((String)e.getValue()).replace('_', '-')))
            .findFirst()
            .orElse(null);
    }

    @Override
    public List<AlbumEntry> fetchAllRoutes(String username, boolean isLoggedIn) {
        var allroutes = new ArrayList<AlbumEntry>();

        var albums = new ArrayList<AlbumEntry>();
        var sql = "select a.*, count(c.albumentry_id) as childcount from albumentries a left join albumentries c on c.parent=a.albumentry_id where a.album=true and (not a.require_login or (a.require_login and a.require_login=?)) group by a.albumentry_id, a.parent, a.localpath, a.album, a.title, a.description, a.imageUrl, a.thumbnailUrl, a.sort, a.lastmodified, a.contenttype, a.contentlength, a.require_login, a.group_by_year order by a.localpath";
        try (var connection = datasource.getConnection()) {
            try (var statement = connection.prepareStatement(sql)) {
                statement.setBoolean(1, isLoggedIn);
                try (var results = statement.executeQuery()) {
                    while (results.next()) {
                        var route = unpackAlbumEntry(results);
                        albums.add(route);
                    }
                }
            }

            for (var album : albums) {
                var imageQuery = "select albumentry_id, parent, localpath, album, title, description, imageurl, thumbnailurl, sort, lastmodified, contenttype, contentlength, require_login, group_by_year from albumentries where album=false and parent=? order by localpath";
                allroutes.add(album);
                try (var statement = connection.prepareStatement(imageQuery)) {
                    statement.setInt(1, album.id());
                    try (var results = statement.executeQuery()) {
                        while (results.next()) {
                            var route = unpackAlbumEntry(results);
                            allroutes.add(route);
                        }
                    }
                }
            }

            if (!isLoggedIn) {
                addAlbumEntriesThatDoNotRequireLoginButHasAParentThatRequiresLogin(allroutes, connection);
            }

        } catch (SQLException e) {
            logger.error("Failed to find the list of all routes", e);
        }

        return allroutes;
    }

    private void addAlbumEntriesThatDoNotRequireLoginButHasAParentThatRequiresLogin(
        List<AlbumEntry> allroutes,
        Connection connection) throws SQLException
    {
        var sql = "select a.* from albumentries a join albumentries p on a.parent=p.albumentry_id where p.require_login and not a.require_login";
        try (var statement = connection.createStatement()) {
            try (var results = statement.executeQuery(sql)) {
                while (results.next()) {
                    var entry = unpackAlbumEntry(results);
                    allroutes.add(entry);
                }
            }
        }
    }

    @Override
    public LinkedHashMap<String, String> findShiroProtectedUrls() {
        var urls = new LinkedHashMap<String, String>();
        try (var connection = datasource.getConnection()) {
            var childrenOfAlbumRequiringLoginThatDoNotRequireLogin = new ArrayList<AlbumEntry>();
            addAlbumEntriesThatDoNotRequireLoginButHasAParentThatRequiresLogin(childrenOfAlbumRequiringLoginThatDoNotRequireLogin, connection);
            for(var entry : childrenOfAlbumRequiringLoginThatDoNotRequireLogin) {
                urls.put(entry.path(), "anon");
            }

            var protectedAlbums = findProtectedAlbums(connection);
            for(var album : protectedAlbums) {
                urls.put(album.path() + "**", "authc");
            }
        } catch (SQLException e) {
            logger.error("Failed to find the list of shiro protected urls", e);
        }
        return urls;
    }

    private List<AlbumEntry> findProtectedAlbums(Connection connection) throws SQLException {
        var protectedAlbums = new ArrayList<AlbumEntry>();
        var sql = "select a.* from albumentries a where album and require_login";
        try (var statement = connection.createStatement()) {
            try (var results = statement.executeQuery(sql)) {
                while (results.next()) {
                    var entry = unpackAlbumEntry(results);
                    protectedAlbums.add(entry);
                }
            }
        }

        return protectedAlbums;
    }

    @Override
    public List<String> getPaths(boolean isLoggedIn) {
        var paths = new ArrayList<String>();
        var sql = "select localpath from albumentries where (not require_login or (require_login and require_login=?)) order by localpath";
        try (var connection = datasource.getConnection()) {
            try (var statement = connection.prepareStatement(sql)) {
                statement.setBoolean(1, isLoggedIn);
                try (var results = statement.executeQuery()) {
                    while(results.next()) {
                        paths.add(results.getString("localpath"));
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Failed to find the list of paths the app can be entered in", e);
        }

        return paths;
    }

    @Override
    public Optional<AlbumEntry> getAlbumEntry(int albumEntryId)  {
        try (var connection = datasource.getConnection()) {
            return getEntry(connection, albumEntryId);
        } catch (SQLException e) {
            logger.warn("Failed to find parent album for batch add of pictures", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<AlbumEntry> getPreviousAlbumEntry(int albumEntryId, boolean isLoggedIn) {
        try (var connection = datasource.getConnection()) {
            return getEntry(connection, albumEntryId)
                .flatMap(entry -> findPreviousEntryInTheSameAlbum(connection, entry, entry.sort(), isLoggedIn));
        } catch (SQLException e) {
            logger.warn(format("Database failure when finding previous album entry for %d", albumEntryId), e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<AlbumEntry> getNextAlbumEntry(int albumEntryId, boolean isLoggedIn) {
        try (var connection = datasource.getConnection()) {
            return getEntry(connection, albumEntryId)
                .flatMap(entry -> findNextEntryInTheSameAlbum(connection, entry, entry.sort(), isLoggedIn));
        } catch (SQLException e) {
            logger.warn(format("Database failure when finding next album entry for %d", albumEntryId), e);
            return Optional.empty();
        }
    }

    @Override
    public AlbumEntry getAlbumEntryFromPath(String path) {
        var sql = "select albumentry_id, parent, localpath, album, title, description, imageurl, thumbnailurl, sort, lastmodified, contenttype, contentlength, require_login, group_by_year from albumentries where localpath=?";
        try (var connection = datasource.getConnection()) {
            try (var statement = connection.prepareStatement(sql)) {
                statement.setString(1, path);
                try (var results = statement.executeQuery()) {
                    while (results.next()) {
                        return unpackAlbumEntry(results);
                    }
                    logger.warn(String.format("Found no albumentry matching path \"%s\"", path));
                }
            }
        } catch (SQLException e) {
            logger.error(String.format("Failed to find albumentry with path \"%s\"", path), e);
        }

        return null;
    }

    @Override
    public List<AlbumEntry> getChildren(int parent, boolean isLoggedIn) {
        var children = new ArrayList<AlbumEntry>();
        var sql = "select albumentry_id, parent, localpath, album, title, description, imageurl, thumbnailurl, sort, lastmodified, contenttype, contentlength, require_login, group_by_year from albumentries where parent=? and (not require_login or (require_login and require_login=?)) order by sort asc";
        try(var connection = datasource.getConnection()) {
            try(var statement = connection.prepareStatement(sql)) {
                statement.setInt(1, parent);
                statement.setBoolean(2, isLoggedIn);
                try(var results = statement.executeQuery()) {
                    while(results.next()) {
                        var child = unpackAlbumEntry(results);
                        children.add(child);
                    }
                }
            }
        } catch (SQLException e) {
            logger.error(String.format("Failed to get list of children for id \"%d\"", parent), e);
        }

        return children;
    }

    List<AlbumEntry> findSelectedentries(List<Integer> selectedentryIds) {
        var selectedentries = new ArrayList<AlbumEntry>();
        var selectedentryIdGroup = selectedentryIds.stream().map(Object::toString).collect(Collectors.joining(","));
        var sql = String.format("select albumentry_id, parent, localpath, album, title, description, imageurl, thumbnailurl, sort, lastmodified, contenttype, contentlength, require_login, group_by_year from albumentries where albumentry_id in (%s)", selectedentryIdGroup);
        try(var connection = datasource.getConnection()) {
            try(var statement = connection.createStatement()) {
                try(var results = statement.executeQuery(sql)) { // NOSONAR no risk of SQL injection here since the joined ids are all integers
                    while(results.next()) {
                        var entry = unpackAlbumEntry(results);
                        selectedentries.add(entry);
                    }
                }
            }
        } catch (SQLException e) {
            logger.error(String.format("Failed to get selection of albumentries for ids \"%s\"", selectedentryIdGroup), e);
        }

        return selectedentries;
    }

    @Override
    public List<AlbumEntry> updateEntry(AlbumEntry modifiedEntry) {
        var id = modifiedEntry.id();
        var sql = "update albumentries set parent=?, localpath=?, title=?, description=?, imageUrl=?, thumbnailUrl=?, lastModified=?, sort=?, require_login=?, group_by_year=? where albumentry_id=?";
        try(var connection = datasource.getConnection()) {
            var sort = adjustSortValuesWhenMovingToDifferentAlbum(connection, modifiedEntry);
            try(var statement = connection.prepareStatement(sql)) {
                statement.setInt(1, modifiedEntry.parent());
                statement.setString(2, modifiedEntry.path());
                statement.setString(3, modifiedEntry.title());
                statement.setString(4, modifiedEntry.description());
                statement.setString(5, modifiedEntry.imageUrl());
                statement.setString(6, modifiedEntry.thumbnailUrl());
                statement.setTimestamp(7, getLastModifiedTimestamp(modifiedEntry));
                statement.setInt(8, sort);
                statement.setBoolean(9, modifiedEntry.requireLogin());
                if (modifiedEntry.groupByYear() == null) {
                    statement.setNull(10, Types.BOOLEAN);
                } else {
                    statement.setBoolean(10, modifiedEntry.groupByYear());
                }
                statement.setInt(11, id);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            logger.error(String.format("Failed to update album entry for id \"%d\"", id), e);
        }

        return fetchAllRoutes(null, true); // All edits are logged in
    }

    @Override
    public List<AlbumEntry> toggleEntryPasswordProtection(int albumEntryId) {
        try(var connection = datasource.getConnection()) {
            Boolean requireLogin = null;
            try(var statement = connection.prepareStatement("select require_login from albumentries where albumentry_id=?")) {
                statement.setInt(1, albumEntryId);
                try (var results = statement.executeQuery()) {
                    while (results.next()) {
                        requireLogin = results.getBoolean("require_login");
                    }
                }
            }

            try(var statement = connection.prepareStatement("update albumentries set require_login=? where albumentry_id=?")) {
                statement.setBoolean(1, !Boolean.TRUE.equals(requireLogin));
                statement.setInt(2, albumEntryId);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            logger.error(String.format("Failed to toggle album entry for login requirement for id \"%d\"", albumEntryId), e);
        }

        return fetchAllRoutes(null, true); // Have to be logged in to be able to toggle login requirement
    }

    @Override
    public List<AlbumEntry> addEntry(AlbumEntry addedEntry) {
        var sql = "insert into albumentries (parent, localpath, album, title, description, imageUrl, thumbnailUrl, sort, lastmodified, contenttype, contentlength, require_login, group_by_year) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        var path = addedEntry.path();
        try(var connection = datasource.getConnection()) {
            try(var statement = connection.prepareStatement(sql)) {
                statement.setInt(1, addedEntry.parent());
                statement.setString(2, path);
                statement.setBoolean(3, addedEntry.album());
                statement.setString(4, addedEntry.title());
                statement.setString(5, addedEntry.description());
                statement.setString(6, addedEntry.imageUrl());
                statement.setString(7, addedEntry.thumbnailUrl());
                statement.setInt(8, addedEntry.sort());
                statement.setTimestamp(9, getLastModifiedTimestamp(addedEntry));
                statement.setString(10, addedEntry.contentType());
                statement.setInt(11, addedEntry.contentLength());
                statement.setBoolean(12, addedEntry.requireLogin());
                if (addedEntry.groupByYear() == null) {
                    statement.setNull(13, Types.BOOLEAN);
                } else {
                    statement.setBoolean(13, addedEntry.groupByYear());
                }
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            logger.error(String.format("Failed to add album entry with path \"%s\"", path), e);
        }

        return fetchAllRoutes(null, true); // All edits are logged in
    }

    @Override
    public List<AlbumEntry> deleteEntry(AlbumEntry deletedEntry) {
        deleteSingleAlbumEntry(deletedEntry);
        return fetchAllRoutes(null, true);
    }

    @Override
    public List<AlbumEntry> deleteSelectedEntries(List<Integer> selection) {
        for(var id : selection) {
            getAlbumEntry(id).ifPresent(this::deleteSingleAlbumEntry);
        }

        return fetchAllRoutes(null, true);
    }

    void deleteSingleAlbumEntry(AlbumEntry deletedEntry) {
        var id = deletedEntry.id();
        var sql = "delete from albumentries where albumentry_id=?";
        var parentOfDeleted = deletedEntry.parent();
        var sortOfDeleted = deletedEntry.sort();
        try(var connection = datasource.getConnection()) {
            try(var statement = connection.prepareStatement(sql)) {
                statement.setInt(1, id);
                statement.executeUpdate();
            }

            adjustSortValuesAfterEntryIsRemoved(connection, parentOfDeleted, sortOfDeleted);
        } catch (SQLException e) {
            logger.error(String.format("Failed to delete album entry with id \"%d\"", id), e);
        }
    }

    @Override
    public List<AlbumEntry> moveEntryUp(AlbumEntry movedEntry) {
        var sort = movedEntry.sort();
        if (sort > 1) {
            var entryId = movedEntry.id();
            try(var connection = datasource.getConnection()) {
                findPreviousEntryInTheSameAlbum(connection, movedEntry, sort, true)
                    .ifPresent(previousEntry -> swapSortAndModifiedTimes(connection, movedEntry, previousEntry));
            } catch (SQLException e) {
                logger.error(String.format("Failed to move album entry with id \"%d\"", entryId), e);
            }
        }

        return fetchAllRoutes(null, true); // All edits are logged in
    }

    @Override
    public List<AlbumEntry> moveEntryDown(AlbumEntry movedEntry) {
        var sort = movedEntry.sort();
        var entryId = movedEntry.id();
        try(var connection = datasource.getConnection()) {
            var numberOfEntriesInAlbum = findNumberOfEntriesInAlbum(connection, movedEntry.parent());
            if (sort < numberOfEntriesInAlbum) {
                findNextEntryInTheSameAlbum(connection, movedEntry, sort, true)
                    .ifPresent(nextEntry -> swapSortAndModifiedTimes(connection, movedEntry, nextEntry));
            }
        } catch (Exception e) {
            logger.error("Failed to move album entry with id \"{}\"", entryId, e);
        }

        return fetchAllRoutes(null, true); // All edits are logged in
    }

    @Override
    public String dumpDatabaseSql(String username, boolean isLoggedn) {
        var outputStream = new ByteArrayOutputStream();
        dumpDatabaseSqlToOutputStream(isLoggedn, outputStream);

        return outputStream.toString(StandardCharsets.UTF_8);
    }

    void dumpDatabaseSqlToOutputStream(boolean isLoggedn, OutputStream outputStream) {
        var sqldumper = new ResultSetSqlDumper();
        var sql = "select albumentry_id, parent, localpath, album, title, description, imageurl, thumbnailurl, sort, lastmodified, contenttype, contentlength, require_login, group_by_year from albumentries where (not require_login or (require_login and require_login=?)) order by albumentry_id";
        try (var connection = datasource.getConnection()) {
            try (var statement = connection.prepareStatement(sql)) {
                statement.setBoolean(1, isLoggedn);
                try (var results = statement.executeQuery()) {
                    sqldumper.dumpResultSetAsSql("sb:saved_albumentries", results, outputStream);
                }
            }
            addSqlToAdjustThePrimaryKeyGeneratorAfterImport(outputStream, connection);
        } catch (SQLException e) {
            logger.error("Failed to find the list of paths the app can be entered in", e);
        } catch (IOException e) {
            logger.error("Failed to write the dumped liquibase changelist for the albumentries", e);
        }
    }

    private void addSqlToAdjustThePrimaryKeyGeneratorAfterImport(OutputStream outputStream, Connection connection) throws SQLException, IOException {
        try (var statement = connection.createStatement()) {
            try (var results = statement.executeQuery("select max(albumentry_id) as max_albumentry_id from albumentries")) {
                while(results.next()) {
                    var lastIdInDump = results.getInt("max_albumentry_id");
                    try(var writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {
                        writer.write(String.format("ALTER TABLE albumentries ALTER COLUMN albumentry_id RESTART WITH %d;%n", lastIdInDump + 1));
                    }
                }
            }
        }
    }

    int adjustSortValuesWhenMovingToDifferentAlbum(Connection connection, AlbumEntry modifiedEntry) {
        var originalSortvalue = modifiedEntry.sort();
        return getEntry(connection, modifiedEntry.id()).map(entryBeforeUpdate -> {
            var originalParent = entryBeforeUpdate != null ? entryBeforeUpdate.parent() : 0;
            if (modifiedEntry.parent() == originalParent) {
                return originalSortvalue;
            }

            var originalSort = entryBeforeUpdate != null ? entryBeforeUpdate.sort() : 0;
            adjustSortValuesAfterEntryIsRemoved(connection, originalParent, originalSort);
            var destinationChildCount = findNumberOfEntriesInAlbum(connection, modifiedEntry.parent());
            return destinationChildCount + 1;
        }).orElse(originalSortvalue);
    }

    int findNumberOfEntriesInAlbum(Connection connection, int parentid) {
        var numberOfEntriesInAlbum = 0;
        var findPreviousEntrySql = "select count(albumentry_id) from albumentries where parent=?";
        try(var statement = connection.prepareStatement(findPreviousEntrySql)) {
            statement.setInt(1, parentid);
            try(var result = statement.executeQuery()) {
                if (result.next()) {
                    numberOfEntriesInAlbum = result.getInt(1);
                }
            }
        } catch (SQLException e) {
            var message = String.format("Failed to find number of entries in album with id=%d", parentid);
            throw new OldAlbumException(message, e);
        }

        return numberOfEntriesInAlbum;
    }

    Optional<AlbumEntry> findPreviousEntryInTheSameAlbum(Connection connection, AlbumEntry movedEntry, int sort, boolean isLoggedIn) {
        Optional<AlbumEntry> previousEntryId = Optional.empty();
        var findPreviousEntrySql = "select albumentry_id, parent, localpath, album, title, description, imageurl, thumbnailurl, sort, lastmodified, contenttype, contentlength, require_login, group_by_year from albumentries where sort<? and parent=? and (not require_login or (require_login and require_login=?)) order by sort desc";
        try(var statement = connection.prepareStatement(findPreviousEntrySql)) {
            statement.setInt(1, sort);
            statement.setInt(2, movedEntry.parent());
            statement.setBoolean(3, isLoggedIn);
            try(var result = statement.executeQuery()) {
                if (result.next()) {
                    previousEntryId = Optional.of(unpackAlbumEntry(result));
                }
            }
        } catch (SQLException e) {
            sneakyThrows(e);
        }

        return previousEntryId;
    }

    Optional<AlbumEntry> findNextEntryInTheSameAlbum(Connection connection, AlbumEntry movedEntry, int sort, boolean isLoggedIn) {
        Optional<AlbumEntry> nextEntryId = Optional.empty();
        var findPreviousEntrySql = "select albumentry_id, parent, localpath, album, title, description, imageurl, thumbnailurl, sort, lastmodified, contenttype, contentlength, require_login, group_by_year from albumentries where sort>? and parent=? and (not require_login or (require_login and require_login=?)) order by sort asc";
        try(var statement = connection.prepareStatement(findPreviousEntrySql)) {
            statement.setInt(1, sort);
            statement.setInt(2, movedEntry.parent());
            statement.setBoolean(3, isLoggedIn);
            try(var result = statement.executeQuery()) {
                if (result.next()) {
                    nextEntryId = Optional.of(unpackAlbumEntry(result));
                }
            }
        } catch (SQLException e) {
            sneakyThrows(e);
        }

        return nextEntryId;
    }

    Optional<AlbumEntry> getEntry(Connection connection, int id) {
        var sql = "select albumentry_id, parent, localpath, album, title, description, imageurl, thumbnailurl, sort, lastmodified, contenttype, contentlength, require_login, group_by_year from albumentries where albumentry_id=?";
        try(var statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try(var result = statement.executeQuery()) {
                if (result.next()) {
                    return Optional.of(unpackAlbumEntry(result));
                }
            }
        } catch (SQLException e) {
            throw new OldAlbumException(String.format("Unable to load album entry matching id=%d from database", id), e);
        }

        return Optional.empty();
    }

    @Override
    public StreamingOutput downloadAlbumEntry(int albumEntryId) {
        var albumEntry = getAlbumEntry(albumEntryId)
            .orElseThrow(() -> new OldAlbumException(String.format("Unable to find album entry matching id=%d in database", albumEntryId)));
        if (albumEntry.album()) {
            return createStreamingZipFileForAlbumContent(albumEntry);
        } else {
            return downloadImageUrlAndStreamImageWithModifiedMetadata(albumEntry);
        }
    }

    @Override
    public StreamingOutput downloadAlbumEntrySelection(List<Integer> selectedentryIds) {
        var selectedentries = findSelectedentries(selectedentryIds);
        return new StreamingOutput() {

            @Override
            public void write(OutputStream output) throws IOException, WebApplicationException {
                try(var zipOut = new ZipOutputStream(output)) {
                    for (var selectedEntry : selectedentries) {
                        var imageAndWriter = downloadAndReadImageAndCreateWriter(selectedEntry);
                        writeImageWithModifiedMetadataToZipArchive(zipOut, selectedEntry, imageAndWriter);
                    }
                }
            }
        };
    }

    StreamingOutput createStreamingZipFileForAlbumContent(AlbumEntry albumEntry) {
        return new StreamingOutput() {

            @Override
            public void write(OutputStream output) throws IOException, WebApplicationException {
                try(var zipOut = new ZipOutputStream(output)) {
                    for (var child : getChildren(albumEntry.id(), false)) {
                        var imageAndWriter = downloadAndReadImageAndCreateWriter(child);
                        writeImageWithModifiedMetadataToZipArchive(zipOut, child, imageAndWriter);
                    }
                }
            }
        };
    }

    void writeImageWithModifiedMetadataToZipArchive(ZipOutputStream zipArchive, AlbumEntry albumEntry, ImageAndWriter imageAndWriter) throws IOException {
        var filename = findFileNamePartOfUrl(albumEntry.imageUrl());
        var entry = new ZipEntry(filename);
        Optional.ofNullable(albumEntry.lastModified()).ifPresent(lm -> entry.setLastModifiedTime(FileTime.fromMillis(lm.getTime())));
        zipArchive.putNextEntry(entry);
        writeImageWithModifiedMetadataToOutputStream(zipArchive, imageAndWriter.writer(), imageAndWriter.image(), albumEntry);
    }

    StreamingOutput downloadImageUrlAndStreamImageWithModifiedMetadata(AlbumEntry albumEntry) {
        var imageAndWriter = downloadAndReadImageAndCreateWriter(albumEntry);
        return writeImageWithModifiedMetadataToTempFile(albumEntry, imageAndWriter.image(), imageAndWriter.writer());
    }

    ImageAndWriter downloadAndReadImageAndCreateWriter(AlbumEntry albumEntry) {
        var imageUrl = albumEntry.imageUrl();
        if (imageUrl == null || imageUrl.isEmpty()) {
            throw new OldAlbumException(String.format("Unable to download album entry matching id=%d, imageUrl is missing", albumEntry.id()));
        }

        ImageAndWriter imageAndWriter = null;
        try {
            var connection = getConnectionFactory().connect(imageUrl);
            connection.setRequestMethod("GET");
            try(var inputStream = ImageIO.createImageInputStream(connection.getInputStream())) {
                var readers = ImageIO.getImageReaders(inputStream);
                if (readers.hasNext()) {
                    var reader = readers.next();
                    var writer = imageIOService.getImageWriter(reader);
                    reader.setInput(inputStream);
                    var image = reader.readAll(0, null);
                    imageAndWriter = new ImageAndWriter(image, writer);
                } else {
                    throw new OldAlbumException(String.format("Album entry matching id=%d with url=\"%s\" not recognizable as an image. Download failed", albumEntry.id(), albumEntry.imageUrl()));
                }
            }
        } catch (IOException e) {
            throw new OldAlbumException(String.format("Unable to download album entry matching id=%d from url=\"%s\"", albumEntry.id(), albumEntry.imageUrl()), e);
        } catch (URISyntaxException e) {
            throw new OldAlbumException(String.format("Unable to parse download album entry url=\"%s\" matching id=%d", albumEntry.imageUrl(), albumEntry.id()), e);
        }

        return imageAndWriter;
    }

    StreamingOutput writeImageWithModifiedMetadataToTempFile(AlbumEntry albumEntry, IIOImage image, ImageWriter writer) {
        return new StreamingOutput() {

            @Override
            public void write(OutputStream output) throws IOException, WebApplicationException {
                writeImageWithModifiedMetadataToOutputStream(output, writer, image, albumEntry);
            }
        };
    }

    void writeImageWithModifiedMetadataToOutputStream(OutputStream output, ImageWriter writer, IIOImage image, AlbumEntry albumEntry) throws IOException {
        var metadataAsTree = (IIOMetadataNode) image.getMetadata().getAsTree("javax_imageio_jpeg_image_1.0");
        var markerSequence = findMarkerSequenceAndCreateIfNotFound(metadataAsTree);
        setJfifCommentFromAlbumEntryDescription(markerSequence, albumEntry);
        var existingExifDirectory = findExistingExifDirectory(markerSequence);
        writeDateTitleAndDescriptionToExifDataStructure(markerSequence, albumEntry, existingExifDirectory);

        try (var outputStream = ImageIO.createImageOutputStream(output)){
            writer.setOutput(outputStream);
            var param = writer.getDefaultWriteParam();
            var modifiedMetadata = writer.getDefaultImageMetadata(ImageTypeSpecifiers.createFromRenderedImage(image.getRenderedImage()), param);
            modifiedMetadata.setFromTree("javax_imageio_jpeg_image_1.0", metadataAsTree);
            image.setMetadata(modifiedMetadata);
            writer.write(image);
        }
    }

    Directory findExistingExifDirectory(IIOMetadataNode markerSequence) {
        var unknown = findFirstUnknownNode(markerSequence);
        if (unknown == null) {
            return null;
        }

        var rawUserObject = (byte[]) unknown.getUserObject();
        var serializedDirectory = removeExifPrefix(rawUserObject);
        try(var input = new ByteArrayImageInputStream((byte[]) serializedDirectory)) {
            return new TIFFReader().read(input);
        } catch (IOException e) {
            logger.info("Unable to parse existing EXIF directory: {}", e);
        }

        return null;
    }

    IIOMetadataNode findFirstUnknownNode(IIOMetadataNode markerSequence) {
        return (IIOMetadataNode) markerSequence.getElementsByTagName("unknown").item(0);
    }

    byte[] removeExifPrefix(byte[] rawUserObject) {
        return Arrays.copyOfRange(rawUserObject, 6, rawUserObject.length - 1);
    }

    void writeDateTitleAndDescriptionToExifDataStructure(
        IIOMetadataNode markerSequence,
        AlbumEntry albumEntry,
        Directory existingExifDirectory) throws IOException
    {
        var hasExistingExifDirectory = existingExifDirectory != null;
        var entries = new ArrayList<Entry>();
        var existingEntriesIterator = Optional.ofNullable(existingExifDirectory).map(Directory::iterator).orElse(Collections.<Entry>emptyIterator());
        existingEntriesIterator.forEachRemaining(entries::add);
        if (albumEntry.lastModified() != null) {
            var formattedDateTime = formatLastModifiedTimeAsExifDateString(albumEntry);
            replaceOrAddEntry(entries, TIFF.TAG_DATE_TIME, TIFF.TYPE_ASCII, formattedDateTime);
            var subDirectoryEntries = findEntriesOfSubdirectory(entries);
            replaceOrAddEntry(subDirectoryEntries, EXIF.TAG_DATE_TIME_ORIGINAL, TIFF.TYPE_ASCII, formattedDateTime);
            replaceOrAddEntry(entries, EXIF_EXIF, TIFF.TYPE_IFD, new IFD(subDirectoryEntries));
        }

        if (!StringUtil.isEmpty(albumEntry.title())) {
            replaceOrAddEntry(entries, TIFF.TAG_IMAGE_DESCRIPTION, TIFF.TYPE_ASCII, albumEntry.title());
        }

        if (!StringUtil.isEmpty(albumEntry.description())) {
            var subDirectoryEntries = findEntriesOfSubdirectory(entries);
            replaceOrAddEntry(subDirectoryEntries, EXIF.TAG_USER_COMMENT, TIFF.TYPE_BYTE, formatExifUserComment(albumEntry.description()));
            replaceOrAddEntry(entries, EXIF_EXIF, TIFF.TYPE_IFD, new IFD(subDirectoryEntries));
        }

        if (entries.isEmpty()) {
            return;
        }

        try (var bytes = new ByteArrayOutputStream()) {
            bytes.write("Exif".getBytes(StandardCharsets.US_ASCII));
            bytes.write(new byte[2]);
            try(var imageOutputStream = new MemoryCacheImageOutputStream(bytes)) {
                new TIFFWriter().write(entries, imageOutputStream);
            }

            if (hasExistingExifDirectory) {
                findFirstUnknownNode(markerSequence).setUserObject(bytes.toByteArray());
            } else {
                var exif = new IIOMetadataNode("unknown");
                exif.setAttribute("MarkerTag", String.valueOf(0xE1)); // APP1 or "225"
                exif.setUserObject(bytes.toByteArray());
                markerSequence.appendChild(exif);
            }
        }

        new XMLSerializer(System.out, "UTF-8").serialize(markerSequence, false);
    }

    ArrayList<Entry> findEntriesOfSubdirectory(ArrayList<Entry> entries) {
        var indexOfExistingIFDDirectory = findIndexOfEntryWithTiffTag(entries, EXIF_EXIF);
        if (indexOfExistingIFDDirectory < 0) {
            return new ArrayList<Entry>();
        } else {
            var ifd = (IFD) entries.get(indexOfExistingIFDDirectory).getValue();
            var subdirectoryEntries = new ArrayList<Entry>();
            ifd.iterator().forEachRemaining(subdirectoryEntries::add);
            return subdirectoryEntries;
        }
    }

    void replaceOrAddEntry(ArrayList<Entry> entries, int tiffTag, short type, Object value) {
        var indexOfEntry = findIndexOfEntryWithTiffTag(entries, tiffTag);
        var entry = new TIFFEntry(tiffTag, type, value);
        if (indexOfEntry < 0) {
            entries.add(entry);
        } else {
            entries.set(indexOfEntry, entry);
        }
    }

    int findIndexOfEntryWithTiffTag(ArrayList<Entry> entries, int tiffTag) {
        int indexOfEntry = 0;
        for (var entry : entries) {
            if (entry.getIdentifier().equals(tiffTag)) {
                return indexOfEntry;
            }

            ++indexOfEntry;
        }

        return -1;
    }

    String formatLastModifiedTimeAsExifDateString(AlbumEntry albumEntry) {
        var exifDateTimeFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
        exifDateTimeFormat.setTimeZone(TimeZone.getTimeZone("Europe/Oslo"));
        return exifDateTimeFormat.format(albumEntry.lastModified());
    }

    public byte[] formatExifUserComment(String userComment) {
        var userCommentInUtf8 = userComment.getBytes(StandardCharsets.UTF_8);
        var userCommentWithTag = new byte[EXIF_ASCII_ENCODING.length + userCommentInUtf8.length];
        System.arraycopy(EXIF_ASCII_ENCODING, 0, userCommentWithTag, 0, EXIF_ASCII_ENCODING.length);
        System.arraycopy(userCommentInUtf8, 0, userCommentWithTag, EXIF_ASCII_ENCODING.length, userCommentInUtf8.length);
        return userCommentWithTag;
    }

    IIOMetadataNode findMarkerSequenceAndCreateIfNotFound(IIOMetadataNode metadataAsTree) {
        new XMLSerializer(System.out, "UTF-8").serialize(metadataAsTree, false);
        var markerSequence = (IIOMetadataNode) metadataAsTree.getElementsByTagName("markerSequence").item(0);
        if (markerSequence == null) {
            markerSequence = new IIOMetadataNode("markerSequence");
            metadataAsTree.appendChild(markerSequence);
        }

        return markerSequence;
    }

    void setJfifCommentFromAlbumEntryDescription(IIOMetadataNode markerSequence, AlbumEntry albumEntry) {
        if (StringUtil.isEmpty(albumEntry.description())) {
            return;
        }

        var comList = markerSequence.getElementsByTagName("com");
        if (comList.getLength() > 0) {
            var com = (IIOMetadataNode) comList.item(0);
            com.setAttribute("comment", albumEntry.description());
        } else {
            var com = new IIOMetadataNode("com");
            com.setAttribute("comment", albumEntry.description());
            markerSequence.appendChild(com);
        }
    }

    String findFileNamePartOfUrl(String imageUrl) {
        var urlComponents = imageUrl.split("/");
        return urlComponents[urlComponents.length - 1];
    }

    public ImageMetadata readMetadata(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            return fetchImageWithHttpAndReadImageMetadata(imageUrl);
        }

        return null;
    }

    ImageMetadata readMetadataOfLocalFile(File downloadFile, HttpURLConnection dummyConnection) throws IOException {
        final var metadataBuilder = ImageMetadata.with();
        try(var input = new FileInputStream(downloadFile)) {
            readAndParseImageMetadata(downloadFile.getName(), metadataBuilder, dummyConnection, input);
        }

        return metadataBuilder.build();
    }

    private ImageMetadata fetchImageWithHttpAndReadImageMetadata(String imageUrl) {
        try {
            final var metadataBuilder = ImageMetadata.with();
            var connection = getConnectionFactory().connect(imageUrl);
            connection.setRequestMethod("GET");
            try(var input = connection.getInputStream()) {
                readAndParseImageMetadata(imageUrl, metadataBuilder, connection, input);
            }

            ifDescriptionIsEmptyTryLookingForInstaloaderTxtDescriptionFile(imageUrl, metadataBuilder);

            return metadataBuilder
                .status(connection.getResponseCode())
                .contentType(connection.getContentType())
                .contentLength(getAndParseContentLengthHeader(connection))
                .build();
        } catch (IOException e) {
            throw new OldAlbumException(String.format("HTTP Connection error when reading metadata for %s", imageUrl), e);
        } catch (URISyntaxException e) {
            throw new OldAlbumException("URL parse error when reading metadata", e);
        }
    }

    private void readAndParseImageMetadata(String imageUrl, final Builder metadataBuilder, HttpURLConnection connection, InputStream inputStream) {
        try(var input = ImageIO.createImageInputStream(inputStream)) {
            metadataBuilder.lastModified(new Date(connection.getLastModified()));
            var readers = ImageIO.getImageReaders(input);
            if (readers.hasNext()) {
                var reader = readers.next();
                try {
                    logger.info("reader class: {}", reader.getClass().getCanonicalName());
                    reader.setInput(input, true);
                    var metadata = reader.getImageMetadata(0);
                    metadataBuilder.description(findJfifComment(metadata));
                } finally {
                    reader.dispose();
                }
            }
            var exifSegment = readSegments(input, JPEG.APP1, "Exif");
            logger.trace("Start extracting EXIF metadata of {}", imageUrl);
            readExifImageMetadata(imageUrl, metadataBuilder, exifSegment);
            logger.trace("Finished extracting EXIF metadata of {}", imageUrl);
        } catch (IOException e) {
            logger.warn(String.format("Error when reading image metadata for %s",  imageUrl), e);
        }
    }

    void readExifImageMetadata(String imageUrl, final Builder metadataBuilder, List<JPEGSegment> exifSegment) {
        exifSegment.stream().map(s -> s.data()).findFirst().ifPresent(exifData -> {
            try {
                exifData.read();
                var exif = (CompoundDirectory) new TIFFReader().read(ImageIO.createImageInputStream(exifData));
                extractMetadataFromExifTags(metadataBuilder, exif, imageUrl);
            } catch (IOException e) {
                throw new OldAlbumException(String.format("Error reading EXIF data of %s",  imageUrl), e);
            }
        });
    }

    private void extractMetadataFromExifTags(final Builder metadataBuilder, CompoundDirectory exif, String imageUrl) {
        for (var entry : exif) {
            logger.trace("EXIF tag identifier: {}  field name: {}  value: {}", entry.getIdentifier(), entry.getFieldName(), entry.getValueAsString());
            if (entry.getIdentifier().equals(EXIF_DATETIME)) {
                extractExifDatetime(metadataBuilder, entry, imageUrl);
            } else if (entry.getIdentifier().equals(EXIF_DESCRIPTION)) {
                metadataBuilder.title(entry.getValueAsString());
            } else if (entry.getIdentifier().equals(EXIF_EXIF)) {
                var nestedExif = (IFD) entry.getValue();
                for (var nestedEntry : nestedExif) {
                    logger.trace("Nested EXIF tag identifier: {}  field name: {}  value: {}", nestedEntry.getIdentifier(), nestedEntry.getFieldName(), nestedEntry.getValueAsString());
                    if (nestedEntry.getIdentifier().equals(EXIF_USER_COMMENT)) {
                        decodeExifUserCommentWithEncoding(metadataBuilder, nestedEntry);
                    }
                }
            }
        }
    }

    void decodeExifUserCommentWithEncoding(final Builder metadataBuilder, Entry entry) {
        var userCommentRaw = (byte[]) entry.getValue();
        var splitUserComment = splitUserCommentInEncodingAndComment(userCommentRaw);
        if (Arrays.compare(splitUserComment.get(0), EXIF_ASCII_ENCODING) == 0) {
            metadataBuilder.description(new String(splitUserComment.get(1), StandardCharsets.UTF_8));
            logger.info("decoded usercomment {}", new String(splitUserComment.get(1), StandardCharsets.UTF_8));
        } else {
            // Start of user comment not a valid EXIF encoding, try UTF-8 on the entire field
            metadataBuilder.description(new String(userCommentRaw, 0, indexOfFirstZeroByte(userCommentRaw), StandardCharsets.UTF_8));
        }
    }

    int indexOfFirstZeroByte(byte[] bytes) {
        int indxexOfFirstZeroByte = 0;
        while (indxexOfFirstZeroByte < bytes.length && bytes[indxexOfFirstZeroByte] != 0) {
            ++indxexOfFirstZeroByte;
        }

        return indxexOfFirstZeroByte;
    }

    void extractExifDatetime(final Builder metadataBuilder, Entry entry, String imageUrl) {
        try {
            var datetime = parseExifDateTimeAtOsloTimezone(entry.getValueAsString());
            metadataBuilder.lastModified(datetime);
        } catch (ParseException e) {
            throw new OldAlbumException(String.format("Error parsing EXIF 306/DateTime entry of %s",  imageUrl), e);
        }
    }

    Date parseExifDateTimeAtOsloTimezone(String datetime) throws ParseException {
        var exifDateTimeFormat = new SimpleDateFormat("yyyy:MM:dd hh:mm:ss");
        exifDateTimeFormat.setTimeZone(TimeZone.getTimeZone("Europe/Oslo"));
        return exifDateTimeFormat.parse(datetime);
    }

    private String findJfifComment(IIOMetadata metadata) {
        var metadataAsTree = metadata.getAsTree("javax_imageio_1.0");
        return findJfifCommentNode(metadataAsTree)
            .map(n -> n.getAttribute("value")).orElse(null);
    }

    Optional<IIOMetadataNode> findJfifCommentNode(Node metadataAsTree) {
        return StreamSupport.stream(iterable(metadataAsTree.getChildNodes()).spliterator(), false)
            .filter(n -> "Text".equals(n.getNodeName()))
            .findFirst()
            .flatMap(n -> StreamSupport.stream(iterable(n.getChildNodes()).spliterator(), false).findFirst());
    }

    public static Iterable<IIOMetadataNode> iterable(final NodeList nodeList) {
        return () -> new Iterator<IIOMetadataNode>() {

                private int index = 0;

                @Override
                public boolean hasNext() {
                    return index < nodeList.getLength();
                }

                @Override
                public IIOMetadataNode next() {
                    if (!hasNext())
                        throw new NoSuchElementException();
                    return (IIOMetadataNode) nodeList.item(index++);
                }
        };
    }

    void ifDescriptionIsEmptyTryLookingForInstaloaderTxtDescriptionFile(
        String imageUrl,
        Builder metadataBuilder)
    {
        if (metadataBuilder.descriptionIsNullOrEmpty()) {
            var descriptionTxtUrl = convertJpegUrlToTxtUrl(imageUrl);
            try {
                var connection = getConnectionFactory().connect(descriptionTxtUrl);
                connection.setRequestMethod("GET");
                try(var input = connection.getInputStream()) {
                    metadataBuilder.description(IOUtils.toString(input, StandardCharsets.UTF_8));
                }
            } catch (IOException e) {
                logger.debug("Failed to load instaloader description file {}", descriptionTxtUrl);
            } catch (URISyntaxException e) {
                logger.debug("Failed to parse URL for instaloader description file {}", descriptionTxtUrl);
            }
        }
    }

    @Override
    public List<AlbumEntry> batchAddPictures(BatchAddPicturesRequest request) {
        var document = loadAndParseIndexHtml(request);
        getAlbumEntry(request.parent()).ifPresent(parent -> {
            var sort = findHighestSortValueInParentAlbum(request.parent());
            var unsortedPictures = new ArrayList<AlbumEntry>();
            var links = document.select("a");
            for (var link: links) {
                if (hrefIsJpeg(link.attr("href"))) {
                    unsortedPictures.add(createPictureFromUrl(link, parent, request.importYear(), request.defaultTitle()));
                }
            }
            var pictures = Boolean.TRUE.equals(request.sortByDate()) ?
                sortPicturesByLastModifiedDateAscending(unsortedPictures) :
                unsortedPictures; // keep current order if sortByDate is null or false
            for (var picture : pictures) {
                ++sort;
                addEntry(AlbumEntry.with(picture).sort(sort).build());
            }
        });

        return fetchAllRoutes(null, true); // All edits are logged in
    }

    private List<AlbumEntry> sortPicturesByLastModifiedDateAscending(ArrayList<AlbumEntry> unsortedPictures) {
        return unsortedPictures.stream().sorted(Comparator.comparing(AlbumEntry::lastModified)).toList();
    }

    @Override
    public List<AlbumEntry> sortByDate(int albumid) {
        try {
            var entriesToSort = new ArrayList<AlbumEntry>();
            try (var connection = datasource.getConnection()) {
                var sql = "select albumentry_id, parent, localpath, album, title, description, imageurl, thumbnailurl, sort, lastmodified, contenttype, contentlength, require_login, group_by_year from albumentries where parent=? order by lastmodified";
                try (var statement = connection.prepareStatement(sql)) {
                    statement.setInt(1, albumid);
                    try (var results = statement.executeQuery()) {
                        while (results.next()) {
                            var route = unpackAlbumEntry(results);
                            entriesToSort.add(route);
                        }
                    }
                }
            }

            var sort = 0;
            try (Connection connection = datasource.getConnection()) {
                var sql = "update albumentries set sort=? where albumentry_id=?";
                try (var statement = connection.prepareStatement(sql)) {
                    for (var albumEntry : entriesToSort) {
                        ++sort;
                        statement.setInt(1, sort);
                        statement.setInt(2, albumEntry.id());
                        statement.addBatch();
                    }
                    statement.executeBatch();
                }
            }
        } catch (SQLException e) {
            throw new OldAlbumException("Failed to fetch album entries to sort", e);
        }

        return fetchAllRoutes(null, true); // All edits are logged in
    }

    @Override
    public Locale defaultLocale() {
        return defaultLocale;
    }

    @Override
    public List<LocaleBean> availableLocales() {
        return Arrays.asList(Locale.forLanguageTag("nb-NO"), Locale.UK).stream().map(l -> LocaleBean.with().locale(l).build()).toList();
    }

    @Override
    public Map<String, String> displayTexts(Locale locale) {
        return transformResourceBundleToMap(locale);
    }

    @Override
    public String displayText(String key, String locale) {
        var active = locale == null || locale.isEmpty() ? defaultLocale : Locale.forLanguageTag(locale.replace('_', '-'));
        var bundle = ResourceBundle.getBundle(DISPLAY_TEXT_RESOURCES, active);
        return bundle.getString(key);
    }

    private AlbumEntry createPictureFromUrl(Element link, AlbumEntry parent, Integer importYear, String defaultTitle) {
        var basename = findBasename(link);
        var path = parent.path() + basename;
        var imageUrl = link.absUrl("href");
        var thumbnailUrl = findThumbnailUrl(link);
        var metadata = readMetadata(imageUrl);
        var lastModified = findLastModifiedDate(metadata, importYear);
        var contenttype = metadata != null ? metadata.contentType() : null;
        var contentlength = metadata != null ? metadata.contentLength() : 0;
        var title = !stringIsNullOrBlank(defaultTitle) ? defaultTitle : safeGetTitleFromMetadata(metadata);
        var description = metadata != null ? metadata.description() : null;
        return AlbumEntry.with()
            .album(false)
            .parent(parent.id())
            .path(path)
            .imageUrl(imageUrl)
            .thumbnailUrl(thumbnailUrl)
            .title(basename)
            .lastModified(lastModified)
            .contentType(contenttype)
            .contentLength(contentlength)
            .title(title)
            .description(description)
            .requireLogin(parent.requireLogin())
            .build();
    }

    boolean stringIsNullOrBlank(String text) {
        return text == null || text.isBlank();
    }

    String safeGetTitleFromMetadata(ImageMetadata metadata) {
        return Optional.ofNullable(metadata).map(ImageMetadata::title).orElse(null);
    }

    Date findLastModifiedDate(ImageMetadata metadata, Integer importYear) {
        if (importYear == null) {
            return metadata != null ? metadata.lastModified() : null;
        }

        var rawDate = metadata != null && metadata.lastModified() != null ? LocalDateTime.ofInstant(metadata.lastModified().toInstant(), ZoneId.systemDefault()) : LocalDateTime.now();
        var adjustedDate = rawDate.withYear(importYear);
        return Date.from(adjustedDate.atZone(ZoneId.systemDefault()).toInstant());
    }

    private String findBasename(Element link) {
        var linktext = link.text();
        if (!linktext.isEmpty()) {
            return linktext.split("\\.")[0];
        }

        var paths = link.attr("href").split("/");
        var filename = paths[paths.length -1];
        return filename.split("\\.")[0];
    }

    String findThumbnailUrl(Element link) {
        var imgs = link.select("img");
        if (imgs.isEmpty()) {
            return null;
        }

        var thumbnailUrl = imgs.get(0).absUrl("src");
        return thumbnailUrl.isEmpty() ? null : thumbnailUrl;
    }

    int findHighestSortValueInParentAlbum(int parent) {
        try (var connection = datasource.getConnection()) {
            var sql = "select max(sort) from albumentries where parent=?";
            try(var statement = connection.prepareStatement(sql)) {
                statement.setInt(1, parent);
                try(var result = statement.executeQuery()) {
                    if (result.next()) {
                        return result.getInt(1);
                    }
                }
            }

            return 0;
        } catch (SQLException e) {
            logger.warn("Failed to find max existing sort value in parent album for batch add of pictures", e);
            return 0;
        }
    }

    private Document loadAndParseIndexHtml(BatchAddPicturesRequest request) {
        Document document = null;
        try {
            var connection = getConnectionFactory().connect(request.batchAddUrl());
            connection.setRequestMethod("GET");
            var statuscode = connection.getResponseCode();
            if (statuscode != 200) {
                throw new OldAlbumException(String.format("Got HTTP error when requesting the batch add pictures URL, statuscode: %d", statuscode));
            }

            document = Jsoup.parse(connection.getInputStream(), "UTF-8", "");
            document.setBaseUri(request.batchAddUrl());
        } catch (IOException e) {
            throw new OldAlbumException(String.format("Got error parsing the content of URL: %s", request.batchAddUrl()), e);
        } catch (URISyntaxException e) {
            throw new OldAlbumException(String.format("Syntax error parsing URL: %s", request.batchAddUrl()), e);
        }

        return document;
    }

    private Timestamp getLastModifiedTimestamp(AlbumEntry albumentry) {
        Timestamp lastmodified = null;
        if (albumentry.lastModified() != null) {
            lastmodified = Timestamp.from(Instant.ofEpochMilli(albumentry.lastModified().getTime()));
        }

        return lastmodified;
    }

    void adjustSortValuesAfterEntryIsRemoved(Connection connection, int parentOfRemovedEntry, int sortOfRemovedEntry) {
        var updateSortSql = "update albumentries set sort=sort-1 where parent=? and sort > ?";
        try(var updateSortStatement = connection.prepareStatement(updateSortSql)) {
            updateSortStatement.setInt(1, parentOfRemovedEntry);
            updateSortStatement.setInt(2, sortOfRemovedEntry);
            updateSortStatement.executeUpdate();
        } catch (SQLException e) {
            var message = String.format("Failed to adjust sort values after removing album item in album with id=%d", parentOfRemovedEntry);
            throw new OldAlbumException(message, e);
        }
    }

    private void swapSortAndModifiedTimes(Connection connection, AlbumEntry movedEntry, AlbumEntry neighbourEntry) {
        if (atLeastOneEntryIsAlbum(movedEntry, neighbourEntry)) {
            swapSortValues(connection, movedEntry.id(), neighbourEntry.sort(), neighbourEntry.id(), movedEntry.sort());
        } else {
            swapSortAndLastModifiedValues(
                connection,
                movedEntry.id(),
                neighbourEntry.sort(),
                neighbourEntry.lastModified(),
                neighbourEntry.id(),
                movedEntry.sort(),
                movedEntry.lastModified());
        }
    }

    boolean atLeastOneEntryIsAlbum(AlbumEntry movedEntry, AlbumEntry neighbourEntry) {
        return movedEntry.album() || neighbourEntry.album();
    }

    void swapSortValues(Connection connection, int entryId, int newIndex, int neighbourEntryId, int newIndexOfNeighbourEntry) {
        var sql = "update albumentries set sort=? where albumentry_id=?";
        try(var statement = connection.prepareStatement(sql)) {
            statement.setInt(1, newIndex);
            statement.setInt(2, entryId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new OldAlbumException(String.format("Failed to update sort value of moved entry %d", entryId), e);
        }

        try(var statement = connection.prepareStatement(sql)) {
            statement.setInt(1, newIndexOfNeighbourEntry);
            statement.setInt(2, neighbourEntryId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new OldAlbumException(String.format("Failed to update sort value of neighbouring entry %d", neighbourEntryId), e);
        }
    }

    void swapSortAndLastModifiedValues(
        Connection connection,
        int entryId,
        int newSort,
        Date newLastModified,
        int neighbourEntryId,
        int newSortOfNeighbourEntry,
        Date newLastModifiedOfNeighbourEntry)
    {
        var sql = "update albumentries set sort=?, lastmodified=? where albumentry_id=?";
        try(var statement = connection.prepareStatement(sql)) {
            statement.setInt(1, newSort);
            statement.setTimestamp(2, Timestamp.from(newLastModified.toInstant()));
            statement.setInt(3, entryId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new OldAlbumException(String.format("Failed to update sort value of moved entry %d", entryId), e);
        }

        try(var statement = connection.prepareStatement(sql)) {
            statement.setInt(1, newSortOfNeighbourEntry);
            statement.setTimestamp(2, Timestamp.from(newLastModifiedOfNeighbourEntry.toInstant()));
            statement.setInt(3, neighbourEntryId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new OldAlbumException(String.format("Failed to update sort value of neighbouring entry %d", neighbourEntryId), e);
        }
    }

    private AlbumEntry unpackAlbumEntry(ResultSet results) throws SQLException {
        return AlbumEntry.with()
            .id(results.getInt("albumentry_id"))
            .parent(results.getInt("parent"))
            .path(results.getString("localpath"))
            .album(results.getBoolean("album"))
            .title(results.getString("title"))
            .description(results.getString("description"))
            .imageUrl(results.getString("imageurl"))
            .thumbnailUrl(results.getString("thumbnailurl"))
            .sort(results.getInt("sort"))
            .lastModified(timestampToDate(results.getTimestamp("lastmodified")))
            .contentType(results.getString("contenttype"))
            .contentLength(results.getInt("contentlength"))
            .requireLogin(results.getBoolean("require_login"))
            .groupByYear(getNullableBoolean(results, "group_by_year"))
            .childcount(findChildCount(results))
            .build();
    }

    private Boolean getNullableBoolean(ResultSet results, String columnName) throws SQLException {
        var result = results.getBoolean(columnName);
        if (results.wasNull()) {
            return null; // NOSONAR this is intentional because null here means not set in the database
        }

        return result;
    }

    private int findChildCount(ResultSet results) throws SQLException {
        var columncount = results.getMetaData().getColumnCount();
        return columncount > 14 ? results.getInt(15) : 0;
    }

    private Date timestampToDate(Timestamp lastmodifiedTimestamp) {
        return lastmodifiedTimestamp != null ? Date.from(lastmodifiedTimestamp.toInstant()) : null;
    }

    private int getAndParseContentLengthHeader(HttpURLConnection connection) {
        var contentLengthHeader = connection.getHeaderField("Content-Length");
        return contentLengthHeader != null ? Integer.parseInt(contentLengthHeader) : 0;
    }

    static boolean hrefIsJpeg(String href) {
        var extension = FilenameUtils.getExtension(href).toLowerCase();
        return "jpg".equals(extension) || "jpeg".equals(extension);
    }

    public static String convertJpegUrlToTxtUrl(String jpegUrl) {
        return FilenameUtils.removeExtension(jpegUrl) + ".txt";
    }

    private HttpConnectionFactory getConnectionFactory() {
        if (connectionFactory == null) {
            connectionFactory = new HttpConnectionFactory() {

                @Override
                public HttpURLConnection connect(String url) throws IOException, URISyntaxException {
                    return (HttpURLConnection) new URI(url).toURL().openConnection();
                }
            };
        }
        return connectionFactory;
    }

    void setConnectionFactory(HttpConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    List<byte[]> splitUserCommentInEncodingAndComment(byte[] userCommentRaw) {
        var encoding = Arrays.copyOf(userCommentRaw, 8);
        logger.info("encoding {}", new String(encoding));
        var comment = Arrays.copyOfRange(userCommentRaw, 8, userCommentRaw.length);
        logger.info("comment {}", new String(comment));
        return Arrays.asList(encoding, comment);
    }

    Map<String, String> transformResourceBundleToMap(Locale locale) {
        var map = new HashMap<String, String>();
        var bundle = ResourceBundle.getBundle(DISPLAY_TEXT_RESOURCES, locale);
        var keys = bundle.getKeys();
        while(keys.hasMoreElements()) {
            String key = keys.nextElement();
            map.put(key, bundle.getString(key));
        }

        return map;
    }

    // Trick to make compiler stop complaining about unhandled checked exceptions in lambdas
    // run from Optional.flatMap (the exceptions are handled in the code containing the optional).
    @SuppressWarnings("unchecked")
    public static <E extends Throwable> void sneakyThrows(Throwable e) throws E {
        throw (E) e;
    }
}
