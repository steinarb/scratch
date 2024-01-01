import { createReducer } from '@reduxjs/toolkit';
import {
    ALLROUTES_FAILURE,
    LOGIN_CHECK_FAILURE,
    LOGIN_FAILURE,
    LOGOUT_FAILURE,
    SAVE_MODIFIED_ALBUM_FAILURE,
    SAVE_ADDED_ALBUM_FAILURE,
    SAVE_MODIFIED_PICTURE_FAILURE,
    SAVE_ADDED_PICTURE_FAILURE,
    IMAGE_METADATA_FAILURE,
    DELETE_ALBUMENTRY_FAILURE,
    MOVE_ALBUMENTRY_UP_FAILURE,
    MOVE_ALBUMENTRY_LEFT_FAILURE,
    MOVE_ALBUMENTRY_DOWN_FAILURE,
    MOVE_ALBUMENTRY_RIGHT_FAILURE,
} from '../reduxactions';

const errorsReducer = createReducer({}, builder => {
    builder
        .addCase(ALLROUTES_FAILURE, (state, action) => ({ ...state, allroutes: action.payload }))
        .addCase(LOGIN_CHECK_FAILURE, (state, action) => ({ ...state, logincheck: action.payload }))
        .addCase(LOGIN_FAILURE, (state, action) => ({ ...state, login: action.payload }))
        .addCase(LOGOUT_FAILURE, (state, action) => ({ ...state, logout: action.payload }))
        .addCase(SAVE_MODIFIED_ALBUM_FAILURE, (state, action) => ({ ...state, modifiedAlbum: action.payload }))
        .addCase(SAVE_ADDED_ALBUM_FAILURE, (state, action) => ({ ...state, addedAlbum: action.payload }))
        .addCase(SAVE_MODIFIED_PICTURE_FAILURE, (state, action) => ({ ...state, modifiedPicture: action.payload }))
        .addCase(SAVE_ADDED_PICTURE_FAILURE, (state, action) => ({ ...state, addedPicture: action.payload }))
        .addCase(IMAGE_METADATA_FAILURE, (state, action) => ({ ...state, metadata: action.payload }))
        .addCase(DELETE_ALBUMENTRY_FAILURE, (state, action) => ({ ...state, deleteItem: action.payload }))
        .addCase(MOVE_ALBUMENTRY_UP_FAILURE, (state, action) => ({ ...state, moveAlbumentryUp: action.payload }))
        .addCase(MOVE_ALBUMENTRY_LEFT_FAILURE, (state, action) => ({ ...state, moveAlbumentryLeft: action.payload }))
        .addCase(MOVE_ALBUMENTRY_DOWN_FAILURE, (state, action) => ({ ...state, moveAlbumentryDown: action.payload }))
        .addCase(MOVE_ALBUMENTRY_RIGHT_FAILURE, (state, action) => ({ ...state, moveAlbumentryRight: action.payload }));
});

export default errorsReducer;
