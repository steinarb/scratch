import { createReducer } from '@reduxjs/toolkit';
import {
    FILL_MODIFY_ALBUM_FORM,
    FILL_ADD_ALBUM_FORM,
    FILL_MODIFY_PICTURE_FORM,
    FILL_ADD_PICTURE_FORM,
    CLEAR_ALBUM_FORM,
    CLEAR_PICTURE_FORM,
} from '../reduxactions';
const initialState = 0;

const albumentryidReducer = createReducer(initialState, builder => {
    builder
        .addCase(FILL_MODIFY_ALBUM_FORM, (state, action) => action.payload.sort)
        .addCase(FILL_ADD_ALBUM_FORM, (state, action) => action.payload.sort)
        .addCase(FILL_MODIFY_PICTURE_FORM, (state, action) => action.payload.sort)
        .addCase(FILL_ADD_PICTURE_FORM, (state, action) => action.payload.sort)
        .addCase(CLEAR_ALBUM_FORM, () => initialState)
        .addCase(CLEAR_PICTURE_FORM, () => initialState);
});

export default albumentryidReducer;
