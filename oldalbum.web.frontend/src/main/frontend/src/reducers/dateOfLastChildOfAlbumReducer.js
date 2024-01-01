import { createReducer } from '@reduxjs/toolkit';
import {
    ALLROUTES_RECEIVE,
    SAVE_MODIFIED_ALBUM_RECEIVE,
    SAVE_ADDED_ALBUM_RECEIVE,
    SAVE_MODIFIED_PICTURE_RECEIVE,
    SAVE_ADDED_PICTURE_RECEIVE,
    BATCH_ADD_PICTURES_FROM_URL_RECEIVE,
    SORT_ALBUM_ENTRIES_BY_DATE_RECEIVE,
    DELETE_ALBUMENTRY_RECEIVE,
    MOVE_ALBUMENTRY_UP_RECEIVE,
    MOVE_ALBUMENTRY_LEFT_RECEIVE,
    MOVE_ALBUMENTRY_DOWN_RECEIVE,
    MOVE_ALBUMENTRY_RIGHT_RECEIVE,
} from '../reduxactions';

const dateOfLastChildOfAlbumReducer = createReducer({}, builder => {
    builder
        .addCase(ALLROUTES_RECEIVE, (state, action) => findDateOfLastChildOfEachAlbum(action.payload))
        .addCase(SAVE_MODIFIED_ALBUM_RECEIVE, (state, action) => findDateOfLastChildOfEachAlbum(action.payload))
        .addCase(SAVE_ADDED_ALBUM_RECEIVE, (state, action) => findDateOfLastChildOfEachAlbum(action.payload))
        .addCase(SAVE_MODIFIED_PICTURE_RECEIVE, (state, action) => findDateOfLastChildOfEachAlbum(action.payload))
        .addCase(SAVE_ADDED_PICTURE_RECEIVE, (state, action) => findDateOfLastChildOfEachAlbum(action.payload))
        .addCase(BATCH_ADD_PICTURES_FROM_URL_RECEIVE, (state, action) => findDateOfLastChildOfEachAlbum(action.payload))
        .addCase(SORT_ALBUM_ENTRIES_BY_DATE_RECEIVE, (state, action) => findDateOfLastChildOfEachAlbum(action.payload))
        .addCase(DELETE_ALBUMENTRY_RECEIVE, (state, action) => findDateOfLastChildOfEachAlbum(action.payload))
        .addCase(MOVE_ALBUMENTRY_UP_RECEIVE, (state, action) => findDateOfLastChildOfEachAlbum(action.payload))
        .addCase(MOVE_ALBUMENTRY_LEFT_RECEIVE, (state, action) => findDateOfLastChildOfEachAlbum(action.payload))
        .addCase(MOVE_ALBUMENTRY_DOWN_RECEIVE, (state, action) => findDateOfLastChildOfEachAlbum(action.payload))
        .addCase(MOVE_ALBUMENTRY_RIGHT_RECEIVE, (state, action) => findDateOfLastChildOfEachAlbum(action.payload));
});

export default dateOfLastChildOfAlbumReducer;

function findDateOfLastChildOfEachAlbum(allroutes) {
    const dateOfLastChildOfEachAlbum = {};
    for (const picture of allroutes.filter(r => !r.album)) {
        const lastDateOfChild = dateOfLastChildOfEachAlbum[picture.parent];
        if (lastDateOfChild === undefined) {
            dateOfLastChildOfEachAlbum[picture.parent] = new Date(picture.lastModified).toISOString();
        } else {
            const pictureLastModified = new Date(picture.lastModified).toISOString();
            if (lastDateOfChild < pictureLastModified) {
                dateOfLastChildOfEachAlbum[picture.parent] = pictureLastModified;
            }
        }
    }

    // Have to loop over albums as many times as there are levelse
    // of albums containing only albums
    const albums = allroutes.filter(r => r.album);
    for (let i = 0; i < 5; i++) {
        scanAlbumsForLastDateOfChild(dateOfLastChildOfEachAlbum, albums);
    }


    return dateOfLastChildOfEachAlbum;
}

function scanAlbumsForLastDateOfChild(dateOfLastChildOfEachAlbum, albums) {
    for (const album of albums) {
        const lastDateOfChild = dateOfLastChildOfEachAlbum[album.parent];
        if (lastDateOfChild === undefined) {
            if (album.lastModified) {
                dateOfLastChildOfEachAlbum[album.parent] = new Date(album.lastModified).toISOString();
            } else {
                dateOfLastChildOfEachAlbum[album.parent] = dateOfLastChildOfEachAlbum[album.id];
            }
        } else {
            if (album.lastModified) {
                if (lastDateOfChild < album.lastModified) {
                    dateOfLastChildOfEachAlbum[album.parent] = new Date(album.lastModified).toISOString();
                }
            } else {
                if (lastDateOfChild < dateOfLastChildOfEachAlbum[album.id]) {
                    dateOfLastChildOfEachAlbum[album.parent] = dateOfLastChildOfEachAlbum[album.id];
                }
            }
        }
    }
}
