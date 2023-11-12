/*
 * Copyright 2020-2023 Steinar Bang
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.TimeZone;
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
import com.twelvemonkeys.imageio.metadata.Entry;
import com.twelvemonkeys.imageio.metadata.exif.EXIF;
import com.twelvemonkeys.imageio.metadata.jpeg.JPEG;
import com.twelvemonkeys.imageio.metadata.jpeg.JPEGSegment;
import com.twelvemonkeys.imageio.metadata.tiff.IFD;
import com.twelvemonkeys.imageio.metadata.tiff.TIFF;
import com.twelvemonkeys.imageio.metadata.tiff.TIFFEntry;
import com.twelvemonkeys.imageio.metadata.tiff.TIFFReader;
import com.twelvemonkeys.imageio.metadata.tiff.TIFFWriter;
import com.twelvemonkeys.imageio.util.ImageTypeSpecifiers;
import com.twelvemonkeys.lang.StringUtil;

import static com.twelvemonkeys.imageio.metadata.jpeg.JPEGSegmentUtil.*;

import no.priv.bang.jdbc.sqldumper.ResultSetSqlDumper;
import no.priv.bang.oldalbum.services.ImageIOService;
import no.priv.bang.oldalbum.services.OldAlbumException;
import no.priv.bang.oldalbum.services.OldAlbumService;
import no.priv.bang.oldalbum.services.bean.AlbumEntry;
import no.priv.bang.oldalbum.services.bean.BatchAddPicturesRequest;
import no.priv.bang.oldalbum.services.bean.ImageMetadata;
import no.priv.bang.oldalbum.services.bean.ImageMetadata.ImageMetadataBuilder;
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
        List<AlbumEntry> allroutes = new ArrayList<>();

        List<AlbumEntry> albums = new ArrayList<>();
        String sql = "select a.*, count(c.albumentry_id) as childcount from albumentries a left join albumentries c on c.parent=a.albumentry_id where a.album=true and (not a.require_login or (a.require_login and a.require_login=?)) group by a.albumentry_id, a.parent, a.localpath, a.album, a.title, a.description, a.imageUrl, a.thumbnailUrl, a.sort, a.lastmodified, a.contenttype, a.contentlength, a.require_login order by a.localpath";
        try (Connection connection = datasource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setBoolean(1, isLoggedIn);
                try (ResultSet results = statement.executeQuery()) {
                    while (results.next()) {
                        AlbumEntry route = unpackAlbumEntry(results);
                        albums.add(route);
                    }
                }
            }
            for (AlbumEntry album : albums) {
                String imageQuery = "select * from albumentries where album=false and parent=? order by localpath";
                allroutes.add(album);
                try (PreparedStatement statement = connection.prepareStatement(imageQuery)) {
                    statement.setInt(1, album.getId());
                    try (ResultSet results = statement.executeQuery()) {
                        while (results.next()) {
                            AlbumEntry route = unpackAlbumEntry(results);
                            allroutes.add(route);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Failed to find the list of all routes", e);
        }
        return allroutes;
    }

    @Override
    public List<String> getPaths(boolean isLoggedIn) {
        List<String> paths = new ArrayList<>();
        String sql = "select localpath from albumentries where (not require_login or (require_login and require_login=?)) order by localpath";
        try (Connection connection = datasource.getConnection()) {
            try (var statement = connection.prepareStatement(sql)) {
                statement.setBoolean(1, isLoggedIn);
                try (ResultSet results = statement.executeQuery()) {
                    while(results.next()) {
                        paths.add(results.getString(1));
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
    public AlbumEntry getAlbumEntryFromPath(String path) {
        String sql = "select * from albumentries where localpath=?";
        try (Connection connection = datasource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, path);
                try (ResultSet results = statement.executeQuery()) {
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
    public List<AlbumEntry> getChildren(int parent) {
        List<AlbumEntry> children = new ArrayList<>();
        String sql = "select * from albumentries where parent=?";
        try(Connection connection = datasource.getConnection()) {
            try(PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, parent);
                try(ResultSet results = statement.executeQuery()) {
                    while(results.next()) {
                        AlbumEntry child = unpackAlbumEntry(results);
                        children.add(child);
                    }
                }
            }
        } catch (SQLException e) {
            logger.error(String.format("Failed to get list of children for id \"%d\"", parent), e);
        }
        return children;
    }

    @Override
    public List<AlbumEntry> updateEntry(AlbumEntry modifiedEntry) {
        int id = modifiedEntry.getId();
        String sql = "update albumentries set parent=?, localpath=?, title=?, description=?, imageUrl=?, thumbnailUrl=?, lastModified=?, sort=?, require_login=? where albumentry_id=?";
        try(Connection connection = datasource.getConnection()) {
            int sort = adjustSortValuesWhenMovingToDifferentAlbum(connection, modifiedEntry);
            try(PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, modifiedEntry.getParent());
                statement.setString(2, modifiedEntry.getPath());
                statement.setString(3, modifiedEntry.getTitle());
                statement.setString(4, modifiedEntry.getDescription());
                statement.setString(5, modifiedEntry.getImageUrl());
                statement.setString(6, modifiedEntry.getThumbnailUrl());
                statement.setTimestamp(7, getLastModifiedTimestamp(modifiedEntry));
                statement.setInt(8, sort);
                statement.setBoolean(9, modifiedEntry.isRequireLogin());
                statement.setInt(10, id);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            logger.error(String.format("Failed to update album entry for id \"%d\"", id), e);
        }
        return fetchAllRoutes(null, true); // All edits are logged in
    }

    @Override
    public List<AlbumEntry> addEntry(AlbumEntry addedEntry) {
        String sql = "insert into albumentries (parent, localpath, album, title, description, imageUrl, thumbnailUrl, sort, lastmodified, contenttype, contentlength, require_login) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String path = addedEntry.getPath();
        try(Connection connection = datasource.getConnection()) {
            try(PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, addedEntry.getParent());
                statement.setString(2, path);
                statement.setBoolean(3, addedEntry.isAlbum());
                statement.setString(4, addedEntry.getTitle());
                statement.setString(5, addedEntry.getDescription());
                statement.setString(6, addedEntry.getImageUrl());
                statement.setString(7, addedEntry.getThumbnailUrl());
                statement.setInt(8, addedEntry.getSort());
                statement.setTimestamp(9, getLastModifiedTimestamp(addedEntry));
                statement.setString(10, addedEntry.getContentType());
                statement.setInt(11, addedEntry.getContentLength());
                statement.setBoolean(12, addedEntry.isRequireLogin());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            logger.error(String.format("Failed to add album entry with path \"%s\"", path), e);
        }
        return fetchAllRoutes(null, true); // All edits are logged in
    }

    @Override
    public List<AlbumEntry> deleteEntry(AlbumEntry deletedEntry) {
        String sql = "delete from albumentries where albumentry_id=?";
        int id = deletedEntry.getId();
        int parentOfDeleted = deletedEntry.getParent();
        int sortOfDeleted = deletedEntry.getSort();
        try(Connection connection = datasource.getConnection()) {
            try(PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, id);
                statement.executeUpdate();
            }
            adjustSortValuesAfterEntryIsRemoved(connection, parentOfDeleted, sortOfDeleted);
        } catch (SQLException e) {
            logger.error(String.format("Failed to delete album entry with id \"%d\"", id), e);
        }
        return fetchAllRoutes(null, true); // All edits are logged in
    }

    @Override
    public List<AlbumEntry> moveEntryUp(AlbumEntry movedEntry) {
        int sort = movedEntry.getSort();
        if (sort > 1) {
            int entryId = movedEntry.getId();
            try(Connection connection = datasource.getConnection()) {
                findPreviousEntryInTheSameAlbum(connection, movedEntry, sort)
                    .ifPresent(previousEntry -> swapSortAndModifiedTimes(connection, movedEntry, previousEntry));
            } catch (SQLException e) {
                logger.error(String.format("Failed to move album entry with id \"%d\"", entryId), e);
            }
        }
        return fetchAllRoutes(null, true); // All edits are logged in
    }

    @Override
    public List<AlbumEntry> moveEntryDown(AlbumEntry movedEntry) {
        int sort = movedEntry.getSort();
        int entryId = movedEntry.getId();
        try(Connection connection = datasource.getConnection()) {
            int numberOfEntriesInAlbum = findNumberOfEntriesInAlbum(connection, movedEntry.getParent());
            if (sort < numberOfEntriesInAlbum) {
                findNextEntryInTheSameAlbum(connection, movedEntry, sort)
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
        String sql = "select * from albumentries where (not require_login or (require_login and require_login=?)) order by albumentry_id";
        try (Connection connection = datasource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setBoolean(1, isLoggedn);
                try (ResultSet results = statement.executeQuery()) {
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
        try (Statement statement = connection.createStatement()) {
            try (ResultSet results = statement.executeQuery("select max(albumentry_id) from albumentries")) {
                while(results.next()) {
                    int lastIdInDump = results.getInt(1);
                    try(var writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {
                        writer.write(String.format("ALTER TABLE albumentries ALTER COLUMN albumentry_id RESTART WITH %d;%n", lastIdInDump + 1));
                    }
                }
            }
        }
    }

    int adjustSortValuesWhenMovingToDifferentAlbum(Connection connection, AlbumEntry modifiedEntry) {
        int originalSortvalue = modifiedEntry.getSort();
        return getEntry(connection, modifiedEntry.getId()).map(entryBeforeUpdate -> {
                int originalParent = entryBeforeUpdate != null ? entryBeforeUpdate.getParent() : 0;
                if (modifiedEntry.getParent() == originalParent) {
                    return originalSortvalue;
                }

                int originalSort = entryBeforeUpdate != null ? entryBeforeUpdate.getSort() : 0;
                adjustSortValuesAfterEntryIsRemoved(connection, originalParent, originalSort);
                int destinationChildCount = findNumberOfEntriesInAlbum(connection, modifiedEntry.getParent());
                return destinationChildCount + 1;
            }).orElse(originalSortvalue);
    }

    int findNumberOfEntriesInAlbum(Connection connection, int parentid) {
        int numberOfEntriesInAlbum = 0;
        String findPreviousEntrySql = "select count(albumentry_id) from albumentries where parent=?";
        try(PreparedStatement statement = connection.prepareStatement(findPreviousEntrySql)) {
            statement.setInt(1, parentid);
            try(ResultSet result = statement.executeQuery()) {
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

    Optional<AlbumEntry> findPreviousEntryInTheSameAlbum(Connection connection, AlbumEntry movedEntry, int sort) throws SQLException {
        Optional<AlbumEntry> previousEntryId = Optional.empty();
        String findPreviousEntrySql = "select * from albumentries where sort=? and parent=?";
        try(PreparedStatement statement = connection.prepareStatement(findPreviousEntrySql)) {
            statement.setInt(1, sort - 1);
            statement.setInt(2, movedEntry.getParent());
            try(ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    previousEntryId = Optional.of(unpackAlbumEntry(result));
                }
            }
        }
        return previousEntryId;
    }

    Optional<AlbumEntry> findNextEntryInTheSameAlbum(Connection connection, AlbumEntry movedEntry, int sort) throws SQLException {
        Optional<AlbumEntry> nextEntryId = Optional.empty();
        String findPreviousEntrySql = "select * from albumentries where sort=? and parent=?";
        try(PreparedStatement statement = connection.prepareStatement(findPreviousEntrySql)) {
            statement.setInt(1, sort + 1);
            statement.setInt(2, movedEntry.getParent());
            try(ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    nextEntryId = Optional.of(unpackAlbumEntry(result));
                }
            }
        }

        return nextEntryId;
    }

    Optional<AlbumEntry> getEntry(Connection connection, int id) {
        String sql = "select * from albumentries where albumentry_id=?";
        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try(ResultSet result = statement.executeQuery()) {
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
    public File downloadAlbumEntry(int albumEntryId) {
        var tempDir = Path.of(System.getProperty("java.io.tmpdir"));
        var albumEntry = getAlbumEntry(albumEntryId)
            .orElseThrow(() -> new OldAlbumException(String.format("Unable to find album entry matching id=%d in database", albumEntryId)));
        if (albumEntry.isAlbum()) {
            return downloardAlbumContentToStagingDirectoryAndCreateZipFile(albumEntry, tempDir);
        } else {
            return downloadImageUrlToTempFile(albumEntry, tempDir);
        }
    }

    private File downloardAlbumContentToStagingDirectoryAndCreateZipFile(AlbumEntry albumEntry, Path tempDir) {
        var stagingDirectory = createAlbumZipFileStagingDirectory(albumEntry, tempDir);
        copyAlbumContentsToStagingDirectory(albumEntry, stagingDirectory);
        return createZipFileFromStagingDirectory(stagingDirectory);
    }

    File createZipFileFromStagingDirectory(Path stagingDirectory) {
        var zipFileName = stagingDirectory.toString() + ".zip";
        var zipFile = new File(zipFileName);
        try(var zipOut = new ZipOutputStream(new FileOutputStream(zipFile))) {
            try(var files = Files.walk(stagingDirectory)) {
                files
                    .map(Path::toFile)
                    .filter(File::isFile)
                    .forEach(f -> addFileEntryToZipArchive(zipFileName, zipOut, f));
            }
        } catch (FileNotFoundException e) {
            throw new OldAlbumException(String.format("Unable to create zip file for downloaded album: %s", zipFileName), e);
        } catch (IOException e) {
            throw new OldAlbumException(String.format("Did not find files to include in zip file for downloaded album: %s", zipFileName), e);
        }

        return zipFile;
    }

    void addFileEntryToZipArchive(String zipFileName, ZipOutputStream zipArchive, File fileToAdd) {
        try {
            var entry = new ZipEntry(fileToAdd.getName());
            entry.setLastModifiedTime(FileTime.fromMillis(fileToAdd.lastModified())) ;
            zipArchive.putNextEntry(entry);
            try (var fis = new FileInputStream(fileToAdd)) {
                byte[] bytes = new byte[1024];
                int length;
                while ((length = fis.read(bytes)) >= 0) {
                    zipArchive.write(bytes, 0, length);
                }
            }
            zipArchive.closeEntry();
        } catch (IOException e) {
            throw new OldAlbumException(String.format("Unable to add item to zip file for downloaded album: %s", zipFileName), e);
        }
    }

    Path createAlbumZipFileStagingDirectory(AlbumEntry albumEntry, Path tempDir) {
        var path = Path.of(albumEntry.getPath());
        var albumDirectoryName = path.getName(path.getNameCount()-1);
        try {
            var stagingDirectory = tempDir.resolve(albumDirectoryName);
            deleteDirectoryAndContentsIfItExists(stagingDirectory);
            return Files.createDirectory(stagingDirectory);
        } catch (IOException e) {
            throw new OldAlbumException(String.format("Failed to create staging directory for album \"%s\"", albumDirectoryName), e);
        }
    }

    boolean deleteDirectoryAndContentsIfItExists(Path albumDirectoryName) {
        if (albumDirectoryName.toFile().exists()) {
            deleteDirectoryAndContents(albumDirectoryName);
            return true;
        }

        return false;
    }

    void deleteDirectoryAndContents(Path albumDirectoryName) {
        try (var files = Files.walk(albumDirectoryName)){
            files
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
        } catch (IOException e) {
            throw new OldAlbumException(String.format("Failed to delete existing staging directory for album \"%s\"", albumDirectoryName), e);
        }
    }

    private void copyAlbumContentsToStagingDirectory(AlbumEntry albumEntry, Path stagingDirectory) {
        for (var child : getChildren(albumEntry.getId())) {
            downloadImageUrlToTempFile(child, stagingDirectory);
        }
    }

    File downloadImageUrlToTempFile(AlbumEntry albumEntry, Path tempDir) {
        var imageUrl = albumEntry.getImageUrl();
        if (imageUrl == null || imageUrl.isEmpty()) {
            throw new OldAlbumException(String.format("Unable to download album entry matching id=%d, imageUrl is missing", albumEntry.getId()));
        }

        var fileName = findFileNamePartOfUrl(imageUrl);
        var tempfile = tempDir.resolve(fileName).toFile();
        IIOImage image = null;
        ImageWriter writer = null;
        try {
            HttpURLConnection connection = getConnectionFactory().connect(imageUrl);
            connection.setRequestMethod("GET");
            try(var inputStream = ImageIO.createImageInputStream(connection.getInputStream())) {
                var readers = ImageIO.getImageReaders(inputStream);
                if (readers.hasNext()) {
                    var reader = readers.next();
                    writer = imageIOService.getImageWriter(reader);
                    reader.setInput(inputStream);
                    image = reader.readAll(0, null);
                } else {
                    throw new OldAlbumException(String.format("Album entry matching id=%d with url=\"%s\" not recognizable as an image. Download failed", albumEntry.getId(), albumEntry.getImageUrl()));
                }
            }
        } catch (IOException e) {
            throw new OldAlbumException(String.format("Unable to download album entry matching id=%d from url=\"%s\"", albumEntry.getId(), albumEntry.getImageUrl()), e);
        }

        return writeImageWithModifiedMetadataToTempFile(tempfile, albumEntry, image, writer);
    }

    File writeImageWithModifiedMetadataToTempFile(File tempfile, AlbumEntry albumEntry, IIOImage image, ImageWriter writer) {
        var metadataAsTree = (IIOMetadataNode) image.getMetadata().getAsTree("javax_imageio_jpeg_image_1.0");
        var markerSequence = findMarkerSequenceAndCreateIfNotFound(metadataAsTree);
        setJfifCommentFromAlbumEntryDescription(markerSequence, albumEntry);
        try {
            writeDateTitleAndDescriptionToExifDataStructure(markerSequence, albumEntry);
            try (var outputStream = ImageIO.createImageOutputStream(new FileOutputStream(tempfile))){
                writer.setOutput(outputStream);
                var param = writer.getDefaultWriteParam();
                var modifiedMetadata = writer.getDefaultImageMetadata(ImageTypeSpecifiers.createFromRenderedImage(image.getRenderedImage()), param);
                modifiedMetadata.setFromTree("javax_imageio_jpeg_image_1.0", metadataAsTree);
                image.setMetadata(modifiedMetadata);
                writer.write(image);
                Files.setLastModifiedTime(tempfile.toPath(), FileTime.from(albumEntry.getLastModified().toInstant()));
                return tempfile;
            }
        } catch (IOException e) {
            throw new OldAlbumException(String.format("Unable to save local copy of album entry matching id=%d from url=\"%s\"", albumEntry.getId(), albumEntry.getImageUrl()), e);
        }
    }

    void writeDateTitleAndDescriptionToExifDataStructure(IIOMetadataNode markerSequence, AlbumEntry albumEntry) throws IOException {
        Collection<Entry> entries = new ArrayList<>();
        if (albumEntry.getLastModified() != null) {
            var formattedDateTime = formatLastModifiedTimeAsExifDateString(albumEntry);
            entries.add(new TIFFEntry(TIFF.TAG_DATE_TIME, formattedDateTime));
            entries.add(new TIFFEntry(EXIF.TAG_DATE_TIME_ORIGINAL, formattedDateTime));
        }

        if (!StringUtil.isEmpty(albumEntry.getTitle())) {
            entries.add(new TIFFEntry(TIFF.TAG_IMAGE_DESCRIPTION, albumEntry.getTitle()));
        }

        if (!StringUtil.isEmpty(albumEntry.getDescription())) {
            entries.add(new TIFFEntry(EXIF.TAG_USER_COMMENT, formatExifUserComment(albumEntry.getDescription())));
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

            IIOMetadataNode exif = new IIOMetadataNode("unknown");
            exif.setAttribute("MarkerTag", String.valueOf(0xE1)); // APP1 or "225"
            exif.setUserObject(bytes.toByteArray());
            markerSequence.appendChild(exif);
        }
    }

    String formatLastModifiedTimeAsExifDateString(AlbumEntry albumEntry) {
        var exifDateTimeFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
        exifDateTimeFormat.setTimeZone(TimeZone.getTimeZone("Europe/Oslo"));
        var datetime = exifDateTimeFormat.format(albumEntry.getLastModified());
        return datetime;
    }

    public byte[] formatExifUserComment(String userComment) {
        var userCommentInUtf8 = userComment.getBytes(StandardCharsets.UTF_8);
        var userCommentWithTag = new byte[EXIF_ASCII_ENCODING.length + userCommentInUtf8.length];
        System.arraycopy(EXIF_ASCII_ENCODING, 0, userCommentWithTag, 0, EXIF_ASCII_ENCODING.length);
        System.arraycopy(userCommentInUtf8, 0, userCommentWithTag, EXIF_ASCII_ENCODING.length, userCommentInUtf8.length);
        return userCommentWithTag;
    }

    IIOMetadataNode findMarkerSequenceAndCreateIfNotFound(IIOMetadataNode metadataAsTree) {
        var markerSequence = (IIOMetadataNode) metadataAsTree.getElementsByTagName("markerSequence").item(0);
        if (markerSequence == null) {
            markerSequence = new IIOMetadataNode("markerSequence");
            metadataAsTree.appendChild(markerSequence);
        }

        return markerSequence;
    }

    void setJfifCommentFromAlbumEntryDescription(IIOMetadataNode markerSequence, AlbumEntry albumEntry) {
        if (StringUtil.isEmpty(albumEntry.getDescription())) {
            return;
        }

        var comList = markerSequence.getElementsByTagName("com");
        if (comList.getLength() > 0) {
            var com = (IIOMetadataNode) comList.item(0);
            com.setAttribute("comment", albumEntry.getDescription());
        } else {
            var com = new IIOMetadataNode("com");
            com.setAttribute("comment", albumEntry.getDescription());
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

    ImageMetadata readMetadataOfLocalFile(File downloadFile, HttpURLConnection dummyConnection) throws FileNotFoundException, IOException {
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

            return metadataBuilder
                .status(connection.getResponseCode())
                .contentType(connection.getContentType())
                .contentLength(getAndParseContentLengthHeader(connection))
                .build();
        } catch (IOException e) {
            throw new OldAlbumException(String.format("HTTP Connection error when reading metadata for %s", imageUrl), e);
        }
    }

    private void readAndParseImageMetadata(String imageUrl, final ImageMetadataBuilder metadataBuilder, HttpURLConnection connection, InputStream inputStream) {
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
            readExifImageMetadata(imageUrl, metadataBuilder, exifSegment);
        } catch (IOException e) {
            logger.warn(String.format("Error when reading image metadata for %s",  imageUrl), e);
        }
    }

    void readExifImageMetadata(String imageUrl, final ImageMetadataBuilder metadataBuilder, List<JPEGSegment> exifSegment) {
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

    private void extractMetadataFromExifTags(final ImageMetadataBuilder metadataBuilder, CompoundDirectory exif, String imageUrl) {
        for (var entry : exif) {
            if (entry.getIdentifier().equals(EXIF_DATETIME)) {
                extractExifDatetime(metadataBuilder, entry, imageUrl);
            } else if (entry.getIdentifier().equals(EXIF_DESCRIPTION)) {
                metadataBuilder.title(entry.getValueAsString());
            } else if (entry.getIdentifier().equals(EXIF_EXIF)) {
                var nestedExif = (IFD) entry.getValue();
                for (var nestedEntry : nestedExif) {
                    if (nestedEntry.getIdentifier().equals(EXIF_USER_COMMENT)) {
                        var userCommentRaw = (byte[]) nestedEntry.getValue();
                        var splitUserComment = splitUserCommentInEncodingAndComment(userCommentRaw);
                        metadataBuilder.description(new String(splitUserComment.get(1), StandardCharsets.UTF_8));
                    }
                }
            }
        }
    }

    void extractExifDatetime(final ImageMetadataBuilder metadataBuilder, Entry entry, String imageUrl) {
        try {
            var exifDateTimeFormat = new SimpleDateFormat("yyyy:MM:dd hh:mm:ss");
            exifDateTimeFormat.setTimeZone(TimeZone.getTimeZone("Europe/Oslo"));
            var datetime = exifDateTimeFormat.parse(entry.getValueAsString());
            metadataBuilder.lastModified(datetime);
        } catch (ParseException e) {
            throw new OldAlbumException(String.format("Error parsing EXIF 306/DateTime entry of %s",  imageUrl), e);
        }
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
    @Override
    public List<AlbumEntry> batchAddPictures(BatchAddPicturesRequest request) {
        Document document = loadAndParseIndexHtml(request);
        getAlbumEntry(request.getParent()).ifPresent(parent -> {
                int sort = findHighestSortValueInParentAlbum(request.getParent());
                var links = document.select("a");
                for (var link: links) {
                    if (!"../".equals(link.attr("href"))) {
                        ++sort;
                        var picture = createPictureFromUrl(link, parent, sort, request.getImportYear());
                        addEntry(picture);
                    }
                }
            });

        return fetchAllRoutes(null, true); // All edits are logged in
    }

    @Override
    public List<AlbumEntry> sortByDate(int albumid) {
        try {
            List<AlbumEntry> entriesToSort = new ArrayList<>();
            try (Connection connection = datasource.getConnection()) {
                String sql = "select * from albumentries where parent=? order by lastmodified";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setInt(1, albumid);
                    try (ResultSet results = statement.executeQuery()) {
                        while (results.next()) {
                            AlbumEntry route = unpackAlbumEntry(results);
                            entriesToSort.add(route);
                        }
                    }
                }
            }

            int sort = 0;
            try (Connection connection = datasource.getConnection()) {
                String sql = "update albumentries set sort=? where albumentry_id=?";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    for (var albumEntry : entriesToSort) {
                        ++sort;
                        statement.setInt(1, sort);
                        statement.setInt(2, albumEntry.getId());
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
        Locale active = locale == null || locale.isEmpty() ? defaultLocale : Locale.forLanguageTag(locale.replace('_', '-'));
        ResourceBundle bundle = ResourceBundle.getBundle(DISPLAY_TEXT_RESOURCES, active);
        return bundle.getString(key);
    }

    private AlbumEntry createPictureFromUrl(Element link, AlbumEntry parent, int sort, Integer importYear) {
        String basename = findBasename(link);
        String path = Paths.get(parent.getPath(), basename).toString();
        String imageUrl = link.absUrl("href");
        String thumbnailUrl = findThumbnailUrl(link);
        var metadata = readMetadata(imageUrl);
        var lastModified = findLastModifiedDate(metadata, importYear);
        var contenttype = metadata != null ? metadata.getContentType() : null;
        var contentlength = metadata != null ? metadata.getContentLength() : 0;
        var description = metadata != null ? metadata.getDescription() : null;
        return AlbumEntry.with()
            .album(false)
            .parent(parent.getId())
            .path(path)
            .imageUrl(imageUrl)
            .thumbnailUrl(thumbnailUrl)
            .title(basename)
            .lastModified(lastModified)
            .contentType(contenttype)
            .contentLength(contentlength)
            .description(description)
            .requireLogin(parent.isRequireLogin())
            .sort(sort)
            .build();
    }

    Date findLastModifiedDate(ImageMetadata metadata, Integer importYear) {
        if (importYear == null) {
            return metadata != null ? metadata.getLastModified() : null;
        }

        var rawDate = metadata != null && metadata.getLastModified() != null ? LocalDateTime.ofInstant(metadata.getLastModified().toInstant(), ZoneId.systemDefault()) : LocalDateTime.now();
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
            String sql = "select max(sort) from albumentries where parent=?";
            try(PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, parent);
                try(ResultSet result = statement.executeQuery()) {
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
            HttpURLConnection connection = getConnectionFactory().connect(request.getBatchAddUrl());
            connection.setRequestMethod("GET");
            int statuscode = connection.getResponseCode();
            if (statuscode != 200) {
                throw new OldAlbumException(String.format("Got HTTP error when requesting the batch add pictures URL, statuscode: %d", statuscode));
            }

            document = Jsoup.parse(connection.getInputStream(), "UTF-8", "");
            document.setBaseUri(request.getBatchAddUrl());
        } catch (IOException e) {
            throw new OldAlbumException(String.format("Got error parsing the content of URL: %s", request.getBatchAddUrl()), e);
        }
        return document;
    }

    private Timestamp getLastModifiedTimestamp(AlbumEntry albumentry) {
        Timestamp lastmodified = null;
        if (!albumentry.isAlbum() && albumentry.getLastModified() != null) {
            lastmodified = Timestamp.from(Instant.ofEpochMilli(albumentry.getLastModified().getTime()));
        }
        return lastmodified;
    }

    void adjustSortValuesAfterEntryIsRemoved(Connection connection, int parentOfRemovedEntry, int sortOfRemovedEntry) {
        String updateSortSql = "update albumentries set sort=sort-1 where parent=? and sort > ?";
        try(PreparedStatement updateSortStatement = connection.prepareStatement(updateSortSql)) {
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
            swapSortValues(connection, movedEntry.getId(), neighbourEntry.getSort(), neighbourEntry.getId(), movedEntry.getSort());
        } else {
            swapSortAndLastModifiedValues(
                connection,
                movedEntry.getId(),
                neighbourEntry.getSort(),
                neighbourEntry.getLastModified(),
                neighbourEntry.getId(),
                movedEntry.getSort(),
                movedEntry.getLastModified());
        }
    }

    boolean atLeastOneEntryIsAlbum(AlbumEntry movedEntry, AlbumEntry neighbourEntry) {
        return movedEntry.isAlbum() || neighbourEntry.isAlbum();
    }

    void swapSortValues(Connection connection, int entryId, int newIndex, int neighbourEntryId, int newIndexOfNeighbourEntry) {
        String sql = "update albumentries set sort=? where albumentry_id=?";
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
        String sql = "update albumentries set sort=?, lastmodified=? where albumentry_id=?";
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
            .childcount(findChildCount(results))
            .build();
    }

    private int findChildCount(ResultSet results) throws SQLException {
        int columncount = results.getMetaData().getColumnCount();
        return columncount > 13 ? results.getInt(14) : 0;
    }

    private Date timestampToDate(Timestamp lastmodifiedTimestamp) {
        return lastmodifiedTimestamp != null ? Date.from(lastmodifiedTimestamp.toInstant()) : null;
    }

    private int getAndParseContentLengthHeader(HttpURLConnection connection) {
        String contentLengthHeader = connection.getHeaderField("Content-Length");
        return contentLengthHeader != null ? Integer.parseInt(contentLengthHeader) : 0;
    }

    private HttpConnectionFactory getConnectionFactory() {
        if (connectionFactory == null) {
            connectionFactory = new HttpConnectionFactory() {

                    @Override
                    public HttpURLConnection connect(String url) throws IOException {
                        return (HttpURLConnection) new URL(url).openConnection();
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
        var comment = Arrays.copyOfRange(userCommentRaw, 8, userCommentRaw.length);
        return Arrays.asList(encoding, comment);
    }

    Map<String, String> transformResourceBundleToMap(Locale locale) {
        Map<String, String> map = new HashMap<>();
        ResourceBundle bundle = ResourceBundle.getBundle(DISPLAY_TEXT_RESOURCES, locale);
        Enumeration<String> keys = bundle.getKeys();
        while(keys.hasMoreElements()) {
            String key = keys.nextElement();
            map.put(key, bundle.getString(key));
        }

        return map;
    }

}
