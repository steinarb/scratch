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
        String sql = "select * from albumentries where album=true order by localpath";
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

	private AlbumEntry unpackAlbumEntry(ResultSet results) throws SQLException {
        int id = results.getInt(1);
        int parent = results.getInt(2);
        String path = results.getString(3);
        boolean album = results.getBoolean(4);
        String title = results.getString(5);
        String description = results.getString(6);
        String imageUrl = results.getString(7);
        String thumbnailUrl = results.getString(8);
        return new AlbumEntry(id, parent, path, album, title, description, imageUrl, thumbnailUrl);
    }

}
