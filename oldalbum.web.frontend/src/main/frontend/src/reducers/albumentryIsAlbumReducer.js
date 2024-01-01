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

const albumentryIsAlbumReducer = createReducer(initialState, builder => {
    builder
        .addCase(FILL_MODIFY_ALBUM_FORM, (state, action) => true)
        .addCase(FILL_ADD_ALBUM_FORM, (state, action) => true)
        .addCase(FILL_MODIFY_PICTURE_FORM, (state, action) => false)
        .addCase(FILL_ADD_PICTURE_FORM, (state, action) => false)
        .addCase(CLEAR_ALBUM_FORM, (state, action) => initialState)
        .addCase(CLEAR_PICTURE_FORM, (state, action) => initialState);
});

export default albumentryIsAlbumReducer;
