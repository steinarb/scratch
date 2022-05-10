import { createReducer } from '@reduxjs/toolkit';
import {
    FILL_MODIFY_PICTURE_FORM,
    FILL_ADD_PICTURE_FORM,
    MODIFY_PICTURE_IMAGEURL_FIELD_CHANGED,
    ADD_PICTURE_IMAGEURL_FIELD_CHANGED,
    CLEAR_PICTURE_FORM,
} from '../reduxactions';
const initialState = '';

const albumentryImageUrlReducer = createReducer(initialState, {
    [FILL_MODIFY_PICTURE_FORM]: (state, action) => action.payload.imageUrl,
    [FILL_ADD_PICTURE_FORM]: (state, action) => action.payload.imageUrl,
    [MODIFY_PICTURE_IMAGEURL_FIELD_CHANGED]: (state, action) => action.payload,
    [ADD_PICTURE_IMAGEURL_FIELD_CHANGED]: (state, action) => action.payload,
    [CLEAR_PICTURE_FORM]: () => initialState,
});

export default albumentryImageUrlReducer;
