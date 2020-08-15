/*
 * Copyright 2020 Steinar Bang
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.LogService;

import no.priv.bang.oldalbum.services.OldAlbumService;
import no.priv.bang.oldalbum.services.bean.AlbumEntry;

@Component(immediate = true)
public class OldAlbumServiceProvider implements OldAlbumService {

    private LogService logservice;
    private DataSource datasource;

    @Reference
    public void setLogService(LogService logservice) {
        this.logservice = logservice;
    }

    @Reference(target = "(osgi.jndi.service.name=jdbc/oldalbum)")
    public void setDataSource(DataSource datasource) {
        this.datasource = datasource;
    }

    @Activate
    public void activate() {
        // Called when component is activated
    }

    @Override
    public List<AlbumEntry> fetchAllRoutes() {
        List<AlbumEntry> allroutes = new ArrayList<>();

        List<AlbumEntry> albums = new ArrayList<>();
        String sql = "select a.*, count(c.albumentry_id) as childcount from albumentries a left join albumentries c on c.parent=a.albumentry_id where a.album=true group by a.albumentry_id, a.parent, a.localpath, a.album, a.title, a.description, a.imageUrl, a.thumbnailUrl, a.sort  order by a.localpath";
        try (Connection connection = datasource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
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
            logservice.log(LogService.LOG_ERROR, "Failed to find the list of all routes", e);
        }
        return allroutes;
    }

    @Override
    public List<String> getPaths() {
        List<String> paths = new ArrayList<>();
        String sql = "select localpath from albumentries order by localpath";
        try (Connection connection = datasource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                try (ResultSet results = statement.executeQuery(sql)) {
                    while(results.next()) {
                        paths.add(results.getString(1));
                    }
                }
            }
        } catch (SQLException e) {
            logservice.log(LogService.LOG_ERROR, "Failed to find the list of paths the app can be entered in", e);
        }
        return paths;
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
                    logservice.log(LogService.LOG_WARNING, String.format("Found no albumentry matching path \"%s\"", path));
                }
            }
        } catch (SQLException e) {
            logservice.log(LogService.LOG_ERROR, String.format("Failed to find albumentry with path \"%s\"", path), e);
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
            logservice.log(LogService.LOG_ERROR, String.format("Failed to get list of children for id \"%d\"", parent), e);
        }
        return children;
    }

    @Override
    public List<AlbumEntry> updateEntry(AlbumEntry modifiedEntry) {
        int id = modifiedEntry.getId();
        String sql = "update albumentries set parent=?, localpath=?, title=?, description=?, imageUrl=?, thumbnailUrl=?, sort=? where albumentry_id=?";
        try(Connection connection = datasource.getConnection()) {
            int sort = adjustSortValuesWhenMovingToDifferentAlbum(connection, modifiedEntry);
            try(PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, modifiedEntry.getParent());
                statement.setString(2, modifiedEntry.getPath());
                statement.setString(3, modifiedEntry.getTitle());
                statement.setString(4, modifiedEntry.getDescription());
                statement.setString(5, modifiedEntry.getImageUrl());
                statement.setString(6, modifiedEntry.getThumbnailUrl());
                statement.setInt(7, sort);
                statement.setInt(8, id);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            logservice.log(LogService.LOG_ERROR, String.format("Failed to update album entry for id \"%d\"", id), e);
        }
        return fetchAllRoutes();
    }

    @Override
    public List<AlbumEntry> addEntry(AlbumEntry addedEntry) {
        String sql = "insert into albumentries (parent, localpath, album, title, description, imageUrl, thumbnailUrl, sort) values (?, ?, ?, ?, ?, ?, ?, ?)";
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
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            logservice.log(LogService.LOG_ERROR, String.format("Failed to add album entry with path \"%s\"", path), e);
        }
        return fetchAllRoutes();
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
            logservice.log(LogService.LOG_ERROR, String.format("Failed to delete album entry with id \"%d\"", id), e);
        }
        return fetchAllRoutes();
    }

    @Override
    public List<AlbumEntry> moveEntryUp(AlbumEntry movedEntry) {
        int sort = movedEntry.getSort();
        if (sort > 1) {
            int entryId = movedEntry.getId();
            try(Connection connection = datasource.getConnection()) {
                int previousEntryId = findPreviousEntryInTheSameAlbum(connection, movedEntry, sort);
                swapSortValues(connection, entryId, sort - 1, previousEntryId, sort);
            } catch (SQLException e) {
                logservice.log(LogService.LOG_ERROR, String.format("Failed to move album entry with id \"%d\"", entryId), e);
            }
        }
        return fetchAllRoutes();
    }

    @Override
    public List<AlbumEntry> moveEntryDown(AlbumEntry movedEntry) {
        int sort = movedEntry.getSort();
        int entryId = movedEntry.getId();
        try(Connection connection = datasource.getConnection()) {
            int numberOfEntriesInAlbum = findNumberOfEntriesInAlbum(connection, movedEntry.getParent());
            if (sort < numberOfEntriesInAlbum) {
                int nextEntryId = findNextEntryInTheSameAlbum(connection, movedEntry, sort);
                swapSortValues(connection, entryId, sort + 1, nextEntryId, sort);
            }
        } catch (SQLException e) {
            logservice.log(LogService.LOG_ERROR, String.format("Failed to move album entry with id \"%d\"", entryId), e);
        }
        return fetchAllRoutes();
    }

    int adjustSortValuesWhenMovingToDifferentAlbum(Connection connection, AlbumEntry modifiedEntry) throws SQLException {
        AlbumEntry entryBeforeUpdate = getEntry(connection, modifiedEntry.getId());
        int sort = modifiedEntry.getSort();
        int originalParent = entryBeforeUpdate != null ? entryBeforeUpdate.getParent() : 0;
        if (modifiedEntry.getParent() != originalParent) {
            int originalSort = entryBeforeUpdate != null ? entryBeforeUpdate.getSort() : 0;
            adjustSortValuesAfterEntryIsRemoved(connection, originalParent, originalSort);
            int destinationChildCount = findNumberOfEntriesInAlbum(connection, modifiedEntry.getParent());
            sort = destinationChildCount + 1;
        }
        return sort;
    }

    int findNumberOfEntriesInAlbum(Connection connection, int parentid) throws SQLException {
        int numberOfEntriesInAlbum = 0;
        String findPreviousEntrySql = "select count(albumentry_id) from albumentries where parent=?";
        try(PreparedStatement statement = connection.prepareStatement(findPreviousEntrySql)) {
            statement.setInt(1, parentid);
            try(ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    numberOfEntriesInAlbum = result.getInt(1);
                }
            }
        }
        return numberOfEntriesInAlbum;
    }

    int findPreviousEntryInTheSameAlbum(Connection connection, AlbumEntry movedEntry, int sort) throws SQLException {
        int previousEntryId = 0;
        String findPreviousEntrySql = "select albumentry_id from albumentries where sort=? and parent=?";
        try(PreparedStatement statement = connection.prepareStatement(findPreviousEntrySql)) {
            statement.setInt(1, sort - 1);
            statement.setInt(2, movedEntry.getParent());
            try(ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    previousEntryId = result.getInt(1);
                }
            }
        }
        return previousEntryId;
    }

    int findNextEntryInTheSameAlbum(Connection connection, AlbumEntry movedEntry, int sort) throws SQLException {
        int nextEntryId = 0;
        String findPreviousEntrySql = "select albumentry_id from albumentries where sort=? and parent=?";
        try(PreparedStatement statement = connection.prepareStatement(findPreviousEntrySql)) {
            statement.setInt(1, sort + 1);
            statement.setInt(2, movedEntry.getParent());
            try(ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    nextEntryId = result.getInt(1);
                }
            }
        }
        return nextEntryId;
    }

    AlbumEntry getEntry(Connection connection, int id) throws SQLException {
        String sql = "select * from albumentries where albumentry_id=?";
        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try(ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return unpackAlbumEntry(result);
                }
            }
        }
        return null;
    }

    private void adjustSortValuesAfterEntryIsRemoved(Connection connection, int parentOfRemovedEntry, int sortOfRemovedEntry) throws SQLException {
        String updateSortSql = "update albumentries set sort=sort-1 where parent=? and sort > ?";
        try(PreparedStatement updateSortStatement = connection.prepareStatement(updateSortSql)) {
            updateSortStatement.setInt(1, parentOfRemovedEntry);
            updateSortStatement.setInt(2, sortOfRemovedEntry);
            updateSortStatement.executeUpdate();
        }
    }

    private void swapSortValues(Connection connection, int entryId, int newIndex, int previousEntryId, int newIndexOfPreviousEntry) throws SQLException {
        String sql = "update albumentries set sort=? where albumentry_id=?";
        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, newIndex);
            statement.setInt(2, entryId);
            statement.executeUpdate();
        }
        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, newIndexOfPreviousEntry);
            statement.setInt(2, previousEntryId);
            statement.executeUpdate();
        }
    }

    private AlbumEntry unpackAlbumEntry(ResultSet results) throws SQLException {
        int id = results.getInt(1);
        int parent = results.getInt(2);
        String path = results.getString(3);
        boolean album = results.getBoolean(4);
        String title = results.getString(5);
        String description = results.getString(6);
        String imageUrl = results.getString(7);
        String thumbnailUrl = results.getString(8);
        int sort = results.getInt(9);
        int columncount = results.getMetaData().getColumnCount();
        int childcount = columncount > 9 ? results.getInt(10) : 0;
        return new AlbumEntry(id, parent, path, album, title, description, imageUrl, thumbnailUrl, sort, childcount);
    }

}
