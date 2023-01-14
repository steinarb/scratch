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
package no.priv.bang.oldalbum.services;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import no.priv.bang.oldalbum.services.bean.AlbumEntry;
import no.priv.bang.oldalbum.services.bean.BatchAddPicturesRequest;
import no.priv.bang.oldalbum.services.bean.ImageMetadata;
import no.priv.bang.oldalbum.services.bean.LocaleBean;

public interface OldAlbumService {

    List<AlbumEntry> fetchAllRoutes(String username, boolean isLoggedIn);

    List<String> getPaths(boolean isLoggedIn);

    AlbumEntry getAlbumEntryFromPath(String path);

    List<AlbumEntry> getChildren(int parent);

    List<AlbumEntry> updateEntry(AlbumEntry modifiedEntry);

    List<AlbumEntry> addEntry(AlbumEntry addedEntry);

    List<AlbumEntry> deleteEntry(AlbumEntry deletedEntry);

    List<AlbumEntry> moveEntryUp(AlbumEntry movedEntry);

    List<AlbumEntry> moveEntryDown(AlbumEntry movedEntry);

    String dumpDatabaseSql(String username, boolean requireLogin);

    ImageMetadata readMetadata(String imageUrl);

    List<AlbumEntry> batchAddPictures(BatchAddPicturesRequest batchAddPicturesRequest);

    List<AlbumEntry> sortByDate(int albumid);

    Locale defaultLocale();

    List<LocaleBean> availableLocales();

    public Map<String, String> displayTexts(Locale locale);

    public String displayText(String key, String locale);

}
