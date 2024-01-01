import { createReducer } from '@reduxjs/toolkit';
import {
    FILL_MODIFY_PICTURE_FORM,
    FILL_ADD_PICTURE_FORM,
    IMAGE_METADATA_RECEIVE,
    CLEAR_PICTURE_FORM,
} from '../reduxactions';
const initialState = '';

const albumentryContentTypeReducer = createReducer(initialState, builder => {
    builder
        .addCase(FILL_MODIFY_PICTURE_FORM, (state, action) => action.payload.contentType)
        .addCase(FILL_ADD_PICTURE_FORM, (state, action) => action.payload.contentType)
        .addCase(IMAGE_METADATA_RECEIVE, (state, action) => action.payload.contentType || '')
        .addCase(CLEAR_PICTURE_FORM, () => initialState);
});

export default albumentryContentTypeReducer;
