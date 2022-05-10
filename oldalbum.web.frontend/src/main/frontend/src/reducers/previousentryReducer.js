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
const previousentryReducer = createReducer({}, {
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

export default previousentryReducer;

function createMapFromIdToArrayOfChildren(allroutes) {
    const previous = {};
    allroutes.forEach(e => previous[e.id] = findPrevious(e, allroutes));
    return previous;
}

function findPrevious(item, allroutes) {
    if (!item.parent) { return undefined; }
    if (item.sort <= 1) { return undefined; }
    const siblings = allroutes.filter(r => r.parent === item.parent).sort((a,b) => a.sort - b.sort);
    return siblings[siblings.findIndex(s => s.id === item.id) - 1];
}
