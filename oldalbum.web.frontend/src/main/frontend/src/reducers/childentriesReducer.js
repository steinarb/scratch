import { createReducer } from '@reduxjs/toolkit';
import {
    ALLROUTES_RECEIVE,
    SAVE_MODIFIED_ALBUM_RECEIVE,
    SAVE_ADDED_ALBUM_RECEIVE,
    SAVE_MODIFIED_PICTURE_RECEIVE,
    SAVE_ADDED_PICTURE_RECEIVE,
    DELETE_ITEM_RECEIVE,
    MOVE_ALBUMENTRY_UP_RECEIVE,
    MOVE_ALBUMENTRY_LEFT_RECEIVE,
    MOVE_ALBUMENTRY_DOWN_RECEIVE,
    MOVE_ALBUMENTRY_RIGHT_RECEIVE,
} from '../reduxactions';

// Creates a map from id to array of children
const childentriesReducer = createReducer({}, {
    [ALLROUTES_RECEIVE]: (state, action) => createMapFromIdToArrayOfChildren(action.payload),
    [SAVE_MODIFIED_ALBUM_RECEIVE]: (state, action) => createMapFromIdToArrayOfChildren(action.payload),
    [SAVE_ADDED_ALBUM_RECEIVE]: (state, action) => createMapFromIdToArrayOfChildren(action.payload),
    [SAVE_MODIFIED_PICTURE_RECEIVE]: (state, action) => createMapFromIdToArrayOfChildren(action.payload),
    [SAVE_ADDED_PICTURE_RECEIVE]: (state, action) => createMapFromIdToArrayOfChildren(action.payload),
    [DELETE_ITEM_RECEIVE]: (state, action) => createMapFromIdToArrayOfChildren(action.payload),
    [MOVE_ALBUMENTRY_UP_RECEIVE]: (state, action) => createMapFromIdToArrayOfChildren(action.payload),
    [MOVE_ALBUMENTRY_LEFT_RECEIVE]: (state, action) => createMapFromIdToArrayOfChildren(action.payload),
    [MOVE_ALBUMENTRY_DOWN_RECEIVE]: (state, action) => createMapFromIdToArrayOfChildren(action.payload),
    [MOVE_ALBUMENTRY_RIGHT_RECEIVE]: (state, action) => createMapFromIdToArrayOfChildren(action.payload),
});

export default childentriesReducer;

function createMapFromIdToArrayOfChildren(allroutes) {
    const children = {};
    allroutes.forEach(e => addChildToParent(children, e));
    return children;
}

function addChildToParent(state, item) {
    const { parent } = item;
    if (parent) {
        if (parent in state) {
            state[parent].push({ ...item });
        } else {
            state[parent] = [{ ...item }];
        }
    }
}
