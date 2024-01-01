import { createReducer } from '@reduxjs/toolkit';
import {
    FILL_MODIFY_ALBUM_FORM,
    FILL_ADD_ALBUM_FORM,
    FILL_MODIFY_PICTURE_FORM,
    FILL_ADD_PICTURE_FORM,
    MODIFY_ALBUM_PARENT_SELECTED,
    MODIFY_PICTURE_PARENT_SELECTED,
    CLEAR_ALBUM_FORM,
    CLEAR_PICTURE_FORM,
} from '../reduxactions';
const initialState = -1;

const albumentryParentReducer = createReducer(initialState, builder => {
    builder
        .addCase(FILL_MODIFY_ALBUM_FORM, (state, action) => action.payload.parent)
        .addCase(FILL_ADD_ALBUM_FORM, (state, action) => action.payload.parent)
        .addCase(FILL_MODIFY_PICTURE_FORM, (state, action) => action.payload.parent)
        .addCase(FILL_ADD_PICTURE_FORM, (state, action) => action.payload.parent)
        .addCase(MODIFY_ALBUM_PARENT_SELECTED, (state, action) => action.payload.id)
        .addCase(MODIFY_PICTURE_PARENT_SELECTED, (state, action) => action.payload.id)
        .addCase(CLEAR_ALBUM_FORM, () => initialState)
        .addCase(CLEAR_PICTURE_FORM, () => initialState);
});

export default albumentryParentReducer;
