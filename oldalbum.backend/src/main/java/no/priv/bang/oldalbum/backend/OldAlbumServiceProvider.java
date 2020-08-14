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
        String sql = "update albumentries set parent=?, localpath=?, title=?, description=?, imageUrl=?, thumbnailUrl=? where albumentry_id=?";
        try(Connection connection = datasource.getConnection()) {
            try(PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, modifiedEntry.getParent());
                statement.setString(2, modifiedEntry.getPath());
                statement.setString(3, modifiedEntry.getTitle());
                statement.setString(4, modifiedEntry.getDescription());
                statement.setString(5, modifiedEntry.getImageUrl());
                statement.setString(6, modifiedEntry.getThumbnailUrl());
                statement.setInt(7, id);
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
        String updateSortSql = "update albumentries set sort=sort-1 where parent=? and sort > ?";
        int id = deletedEntry.getId();
        int parentOfDeleted = deletedEntry.getParent();
        int sortOfDeleted = deletedEntry.getSort();
        try(Connection connection = datasource.getConnection()) {
            try(PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, id);
                statement.executeUpdate();
            }
            try(PreparedStatement updateSortStatement = connection.prepareStatement(updateSortSql)) {
                updateSortStatement.setInt(1, parentOfDeleted);
                updateSortStatement.setInt(2, sortOfDeleted);
                updateSortStatement.executeUpdate();
            }
        } catch (SQLException e) {
            logservice.log(LogService.LOG_ERROR, String.format("Failed to delete album entry with id \"%d\"", id), e);
        }
        return fetchAllRoutes();
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
