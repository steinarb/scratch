import { createReducer } from '@reduxjs/toolkit';
import {
    FILL_MODIFY_PICTURE_FORM,
    FILL_ADD_PICTURE_FORM,
    IMAGE_METADATA_RECEIVE,
    CLEAR_PICTURE_FORM,
} from '../reduxactions';
const initialState = '';

const albumentryContentLengthReducer = createReducer(initialState, {
    [FILL_MODIFY_PICTURE_FORM]: (state, action) => action.payload.contentLength,
    [FILL_ADD_PICTURE_FORM]: (state, action) => action.payload.contentLength,
    [IMAGE_METADATA_RECEIVE]: (state, action) => action.payload.contentLength || 0,
    [CLEAR_PICTURE_FORM]: () => initialState,
});

export default albumentryContentLengthReducer;
