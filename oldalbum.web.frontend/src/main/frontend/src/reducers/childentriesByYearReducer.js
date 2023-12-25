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

// Creates a map from id to array of children
const childentriesByYearReducer = createReducer({}, {
    [ALLROUTES_RECEIVE]: (state, action) => createMapFromIdToMapOfYearWithArrayOfChildren(action.payload),
    [SAVE_MODIFIED_ALBUM_RECEIVE]: (state, action) => createMapFromIdToMapOfYearWithArrayOfChildren(action.payload),
    [SAVE_ADDED_ALBUM_RECEIVE]: (state, action) => createMapFromIdToMapOfYearWithArrayOfChildren(action.payload),
    [SAVE_MODIFIED_PICTURE_RECEIVE]: (state, action) => createMapFromIdToMapOfYearWithArrayOfChildren(action.payload),
    [SAVE_ADDED_PICTURE_RECEIVE]: (state, action) => createMapFromIdToMapOfYearWithArrayOfChildren(action.payload),
    [BATCH_ADD_PICTURES_FROM_URL_RECEIVE]: (state, action) => createMapFromIdToMapOfYearWithArrayOfChildren(action.payload),
    [SORT_ALBUM_ENTRIES_BY_DATE_RECEIVE]: (state, action) => createMapFromIdToMapOfYearWithArrayOfChildren(action.payload),
    [DELETE_ALBUMENTRY_RECEIVE]: (state, action) => createMapFromIdToMapOfYearWithArrayOfChildren(action.payload),
    [MOVE_ALBUMENTRY_UP_RECEIVE]: (state, action) => createMapFromIdToMapOfYearWithArrayOfChildren(action.payload),
    [MOVE_ALBUMENTRY_LEFT_RECEIVE]: (state, action) => createMapFromIdToMapOfYearWithArrayOfChildren(action.payload),
    [MOVE_ALBUMENTRY_DOWN_RECEIVE]: (state, action) => createMapFromIdToMapOfYearWithArrayOfChildren(action.payload),
    [MOVE_ALBUMENTRY_RIGHT_RECEIVE]: (state, action) => createMapFromIdToMapOfYearWithArrayOfChildren(action.payload),
});

export default childentriesByYearReducer;

function createMapFromIdToMapOfYearWithArrayOfChildren(allroutes) {
    const children = {};
    allroutes.forEach(e => addChildToParent(children, e));
    return children;
}

function addChildToParent(state, item) {
    const { parent, lastModified } = item;
    const year = lastModified ? new Date(lastModified).getFullYear().toString() : new Date().getFullYear().toString();
    if (parent) {
        if (parent in state) {
            if (year in state[parent]) {
                state[parent][year].push({ ...item });
            } else {
                state[parent][year] = [{ ...item }];
            }
        } else {
            state[parent] = {};
            state[parent][year] = [{ ...item }];
        }
    }
}
