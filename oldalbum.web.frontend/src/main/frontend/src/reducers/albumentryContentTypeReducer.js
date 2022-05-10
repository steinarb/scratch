import { createReducer } from '@reduxjs/toolkit';
import {
    IMAGE_METADATA_RECEIVE,
    CLEAR_PICTURE_FORM,
} from '../reduxactions';
const initialState = '';

const albumentryContentTypeReducer = createReducer(initialState, {
    [IMAGE_METADATA_RECEIVE]: (state, action) => action.payload.contentType,
    [CLEAR_PICTURE_FORM]: () => initialState,
});

export default albumentryContentTypeReducer;
