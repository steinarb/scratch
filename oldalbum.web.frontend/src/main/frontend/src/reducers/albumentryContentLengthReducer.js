import { createReducer } from '@reduxjs/toolkit';
import {
    IMAGE_METADATA_RECEIVE,
    CLEAR_PICTURE_FORM,
} from '../reduxactions';
const initialState = '';

const albumentryContentLengthReducer = createReducer(initialState, {
    [IMAGE_METADATA_RECEIVE]: (state, action) => action.payload.contentLength,
    [CLEAR_PICTURE_FORM]: () => initialState,
});

export default albumentryContentLengthReducer;
