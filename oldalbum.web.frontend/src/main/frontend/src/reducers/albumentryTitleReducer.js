import { createReducer } from '@reduxjs/toolkit';
import {
    FILL_MODIFY_ALBUM_FORM,
    FILL_ADD_ALBUM_FORM,
    FILL_MODIFY_PICTURE_FORM,
    FILL_ADD_PICTURE_FORM,
    IMAGE_METADATA_RECEIVE,
    MODIFY_ALBUM_TITLE_FIELD_CHANGED,
    ADD_ALBUM_TITLE_FIELD_CHANGED,
    MODIFY_PICTURE_TITLE_FIELD_CHANGED,
    ADD_PICTURE_TITLE_FIELD_CHANGED,
    CLEAR_ALBUM_FORM,
    CLEAR_PICTURE_FORM,
} from '../reduxactions';
const initialState = '';

const albumentryTitleReducer = createReducer(initialState, builder => {
    builder
        .addCase(FILL_MODIFY_ALBUM_FORM, (state, action) => action.payload.title)
        .addCase(FILL_ADD_ALBUM_FORM, (state, action) => action.payload.title)
        .addCase(FILL_MODIFY_PICTURE_FORM, (state, action) => action.payload.title)
        .addCase(FILL_ADD_PICTURE_FORM, (state, action) => action.payload.title)
        .addCase(IMAGE_METADATA_RECEIVE, (state, action) => action.payload.title || '')
        .addCase(MODIFY_ALBUM_TITLE_FIELD_CHANGED, (state, action) => action.payload)
        .addCase(ADD_ALBUM_TITLE_FIELD_CHANGED, (state, action) => action.payload)
        .addCase(MODIFY_PICTURE_TITLE_FIELD_CHANGED, (state, action) => action.payload)
        .addCase(ADD_PICTURE_TITLE_FIELD_CHANGED, (state, action) => action.payload)
        .addCase(CLEAR_ALBUM_FORM, () => initialState)
        .addCase(CLEAR_PICTURE_FORM, () => initialState);
});

export default albumentryTitleReducer;
