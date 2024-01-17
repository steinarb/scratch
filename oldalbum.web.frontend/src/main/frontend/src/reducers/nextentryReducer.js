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
    DELETE_SELECTION_RECEIVE,
    MOVE_ALBUMENTRY_UP_RECEIVE,
    MOVE_ALBUMENTRY_LEFT_RECEIVE,
    MOVE_ALBUMENTRY_DOWN_RECEIVE,
    MOVE_ALBUMENTRY_RIGHT_RECEIVE,
} from '../reduxactions';

// Creates a map from id to array of children
const nextentryReducer = createReducer({}, (builder) => {
    builder
        .addCase(ALLROUTES_RECEIVE, (state, action) => createMapFromIdToArrayOfChildren(action.payload))
        .addCase(SAVE_MODIFIED_ALBUM_RECEIVE, (state, action) => createMapFromIdToArrayOfChildren(action.payload))
        .addCase(SAVE_ADDED_ALBUM_RECEIVE, (state, action) => createMapFromIdToArrayOfChildren(action.payload))
        .addCase(SAVE_MODIFIED_PICTURE_RECEIVE, (state, action) => createMapFromIdToArrayOfChildren(action.payload))
        .addCase(SAVE_ADDED_PICTURE_RECEIVE, (state, action) => createMapFromIdToArrayOfChildren(action.payload))
        .addCase(BATCH_ADD_PICTURES_FROM_URL_RECEIVE, (state, action) => createMapFromIdToArrayOfChildren(action.payload))
        .addCase(SORT_ALBUM_ENTRIES_BY_DATE_RECEIVE, (state, action) => createMapFromIdToArrayOfChildren(action.payload))
        .addCase(DELETE_ALBUMENTRY_RECEIVE, (state, action) => createMapFromIdToArrayOfChildren(action.payload))
        .addCase(DELETE_SELECTION_RECEIVE, (state, action) => createMapFromIdToArrayOfChildren(action.payload))
        .addCase(MOVE_ALBUMENTRY_UP_RECEIVE, (state, action) => createMapFromIdToArrayOfChildren(action.payload))
        .addCase(MOVE_ALBUMENTRY_LEFT_RECEIVE, (state, action) => createMapFromIdToArrayOfChildren(action.payload))
        .addCase(MOVE_ALBUMENTRY_DOWN_RECEIVE, (state, action) => createMapFromIdToArrayOfChildren(action.payload))
        .addCase(MOVE_ALBUMENTRY_RIGHT_RECEIVE, (state, action) => createMapFromIdToArrayOfChildren(action.payload));
});

export default nextentryReducer;

function createMapFromIdToArrayOfChildren(allroutes) {
    const next = {};
    allroutes.forEach(e => next[e.id] = findNext(e, allroutes));
    return next;
}

function findNext(item, allroutes) {
    if (!item.parent) { return undefined; }
    const parent = allroutes.find(r => r.id === item.parent) || {};
    if (item.sort >= parent.childcount) { return undefined; }
    const siblings = allroutes.filter(r => r.parent === item.parent).sort((a,b) => a.sort - b.sort);
    return siblings[siblings.findIndex(s => s.id === item.id) + 1];
}
