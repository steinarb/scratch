import { createReducer } from '@reduxjs/toolkit';
import {
    IMAGE_METADATA_RECEIVE,
    CLEAR_PICTURE_FORM,
} from '../reduxactions';
const initialState = '';

const albumentryLastModifiedReducer = createReducer(initialState, {
    [IMAGE_METADATA_RECEIVE]: (state, action) => action.payload.lastModified,
    [CLEAR_PICTURE_FORM]: () => initialState,
});

export default albumentryLastModifiedReducer;
