import { createReducer } from '@reduxjs/toolkit';
import {
    ALLROUTES_RECEIVE,
    SAVE_MODIFIED_ALBUM_RECEIVE,
    SAVE_ADDED_ALBUM_RECEIVE,
    SAVE_MODIFIED_PICTURE_RECEIVE,
    SAVE_ADDED_PICTURE_RECEIVE,
    BATCH_ADD_PICTURES_FROM_URL_RECEIVE,
    SORT_ALBUM_ENTRIES_BY_DATE_RECEIVE,
    DELETE_ITEM_RECEIVE,
    MOVE_ALBUMENTRY_UP_RECEIVE,
    MOVE_ALBUMENTRY_LEFT_RECEIVE,
    MOVE_ALBUMENTRY_DOWN_RECEIVE,
    MOVE_ALBUMENTRY_RIGHT_RECEIVE,
} from '../reduxactions';

const albumentriesReducer = createReducer({}, {
    [ALLROUTES_RECEIVE]: (state, action) => createMapFromIdToAlbumentry(action.payload),
    [SAVE_MODIFIED_ALBUM_RECEIVE]: (state, action) => createMapFromIdToAlbumentry(action.payload),
    [SAVE_ADDED_ALBUM_RECEIVE]: (state, action) => createMapFromIdToAlbumentry(action.payload),
    [SAVE_MODIFIED_PICTURE_RECEIVE]: (state, action) => createMapFromIdToAlbumentry(action.payload),
    [SAVE_ADDED_PICTURE_RECEIVE]: (state, action) => createMapFromIdToAlbumentry(action.payload),
    [BATCH_ADD_PICTURES_FROM_URL_RECEIVE]: (state, action) => createMapFromIdToAlbumentry(action.payload),
    [SORT_ALBUM_ENTRIES_BY_DATE_RECEIVE]: (state, action) => createMapFromIdToAlbumentry(action.payload),
    [DELETE_ITEM_RECEIVE]: (state, action) => createMapFromIdToAlbumentry(action.payload),
    [MOVE_ALBUMENTRY_UP_RECEIVE]: (state, action) => createMapFromIdToAlbumentry(action.payload),
    [MOVE_ALBUMENTRY_LEFT_RECEIVE]: (state, action) => createMapFromIdToAlbumentry(action.payload),
    [MOVE_ALBUMENTRY_DOWN_RECEIVE]: (state, action) => createMapFromIdToAlbumentry(action.payload),
    [MOVE_ALBUMENTRY_RIGHT_RECEIVE]: (state, action) => createMapFromIdToAlbumentry(action.payload),
});

export default albumentriesReducer;

function createMapFromIdToAlbumentry(allroutes) {
    const entries = {};
    allroutes.forEach(e => entries[e.id] = { ...e });
    return entries;
}
