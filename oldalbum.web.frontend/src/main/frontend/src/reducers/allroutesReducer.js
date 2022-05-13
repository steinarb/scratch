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

const allroutesReducer = createReducer([], {
    [ALLROUTES_RECEIVE]: (state, action) => action.payload,
    [SAVE_MODIFIED_ALBUM_RECEIVE]: (state, action) => action.payload,
    [SAVE_ADDED_ALBUM_RECEIVE]: (state, action) => action.payload,
    [SAVE_MODIFIED_PICTURE_RECEIVE]: (state, action) => action.payload,
    [SAVE_ADDED_PICTURE_RECEIVE]: (state, action) => action.payload,
    [DELETE_ITEM_RECEIVE]: (state, action) => action.payload,
    [MOVE_ALBUMENTRY_UP_RECEIVE]: (state, action) => action.payload,
    [MOVE_ALBUMENTRY_LEFT_RECEIVE]: (state, action) => action.payload,
    [MOVE_ALBUMENTRY_DOWN_RECEIVE]: (state, action) => action.payload,
    [MOVE_ALBUMENTRY_RIGHT_RECEIVE]: (state, action) => action.payload,
});

export default allroutesReducer;
