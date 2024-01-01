import { createReducer } from '@reduxjs/toolkit';
import {
    FILL_MODIFY_PICTURE_FORM,
    FILL_ADD_PICTURE_FORM,
    MODIFY_PICTURE_THUMBNAILURL_FIELD_CHANGED,
    ADD_PICTURE_THUMBNAILURL_FIELD_CHANGED,
    CLEAR_PICTURE_FORM,
} from '../reduxactions';
const initialState = '';

const albumentryThumbnailUrlReducer = createReducer(initialState, builder => {
    builder
        .addCase(FILL_MODIFY_PICTURE_FORM, (state, action) => action.payload.thumbnailUrl)
        .addCase(FILL_ADD_PICTURE_FORM, (state, action) => action.payload.thumbnailUrl)
        .addCase(MODIFY_PICTURE_THUMBNAILURL_FIELD_CHANGED, (state, action) => action.payload)
        .addCase(ADD_PICTURE_THUMBNAILURL_FIELD_CHANGED, (state, action) => action.payload)
        .addCase(CLEAR_PICTURE_FORM, () => initialState);
});

export default albumentryThumbnailUrlReducer;
