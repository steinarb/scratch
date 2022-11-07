import { createReducer } from '@reduxjs/toolkit';
import {
    FILL_ADD_PICTURE_FORM,
    IMAGE_METADATA_RECEIVE,
    CLEAR_PICTURE_FORM,
} from '../reduxactions';
const initialState = '';

const albumentryLastModifiedReducer = createReducer(initialState, {
    [FILL_ADD_PICTURE_FORM]: (state, action) => action.payload.lastModified,
    [IMAGE_METADATA_RECEIVE]: (state, action) => action.payload.lastModified,
    [CLEAR_PICTURE_FORM]: () => initialState,
});

export default albumentryLastModifiedReducer;
