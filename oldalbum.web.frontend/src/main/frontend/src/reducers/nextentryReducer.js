import { createReducer } from '@reduxjs/toolkit';
import {
    ALLROUTES_RECEIVE,
    SAVE_MODIFIED_ALBUM_RECEIVE,
    SAVE_ADDED_ALBUM_RECEIVE,
    SAVE_MODIFIED_PICTURE_RECEIVE,
    DELETE_ITEM_RECEIVE,
    MOVE_ALBUMENTRY_UP_RECEIVE,
    MOVE_ALBUMENTRY_LEFT_RECEIVE,
    MOVE_ALBUMENTRY_DOWN_RECEIVE,
    MOVE_ALBUMENTRY_RIGHT_RECEIVE,
} from '../reduxactions';

// Creates a map from id to array of children
const nextentryReducer = createReducer({}, {
    [ALLROUTES_RECEIVE]: (state, action) => createMapFromIdToArrayOfChildren(action.payload),
    [SAVE_MODIFIED_ALBUM_RECEIVE]: (state, action) => createMapFromIdToArrayOfChildren(action.payload),
    [SAVE_ADDED_ALBUM_RECEIVE]: (state, action) => createMapFromIdToArrayOfChildren(action.payload),
    [SAVE_MODIFIED_PICTURE_RECEIVE]: (state, action) => createMapFromIdToArrayOfChildren(action.payload),
    [DELETE_ITEM_RECEIVE]: (state, action) => createMapFromIdToArrayOfChildren(action.payload),
    [MOVE_ALBUMENTRY_UP_RECEIVE]: (state, action) => createMapFromIdToArrayOfChildren(action.payload),
    [MOVE_ALBUMENTRY_LEFT_RECEIVE]: (state, action) => createMapFromIdToArrayOfChildren(action.payload),
    [MOVE_ALBUMENTRY_DOWN_RECEIVE]: (state, action) => createMapFromIdToArrayOfChildren(action.payload),
    [MOVE_ALBUMENTRY_RIGHT_RECEIVE]: (state, action) => createMapFromIdToArrayOfChildren(action.payload),
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
