import { createReducer } from '@reduxjs/toolkit';
import {
    FILL_MODIFY_ALBUM_FORM,
    FILL_ADD_ALBUM_FORM,
    FILL_MODIFY_PICTURE_FORM,
    FILL_ADD_PICTURE_FORM,
    CLEAR_ALBUM_FORM,
    CLEAR_PICTURE_FORM,
} from '../reduxactions';
const initialState = false;

const albumentryIsAlbumReducer = createReducer(initialState, {
    [FILL_MODIFY_ALBUM_FORM]: (state, action) => true,
    [FILL_ADD_ALBUM_FORM]: (state, action) => true,
    [FILL_MODIFY_PICTURE_FORM]: (state, action) => false,
    [FILL_ADD_PICTURE_FORM]: (state, action) => false,
    [CLEAR_ALBUM_FORM]: (state, action) => initialState,
    [CLEAR_PICTURE_FORM]: (state, action) => initialState,
});

export default albumentryIsAlbumReducer;
