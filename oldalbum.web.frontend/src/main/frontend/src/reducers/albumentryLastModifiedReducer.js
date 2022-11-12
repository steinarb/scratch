import { createReducer } from '@reduxjs/toolkit';
import {
    FILL_MODIFY_PICTURE_FORM,
    FILL_ADD_PICTURE_FORM,
    IMAGE_METADATA_RECEIVE,
    MODIFY_PICTURE_LASTMODIFIED_FIELD_CHANGED,
    ADD_PICTURE_LASTMODIFIED_FIELD_CHANGED,
    CLEAR_PICTURE_FORM,
} from '../reduxactions';
const initialState = '';

const albumentryLastModifiedReducer = createReducer(initialState, {
    [FILL_MODIFY_PICTURE_FORM]: (state, action) => new Date(action.payload.lastModified).toISOString(),
    [FILL_ADD_PICTURE_FORM]: (state, action) => action.payload.lastModified,
    [IMAGE_METADATA_RECEIVE]: (state, action) => new Date(action.payload.lastModified).toISOString(),
    [MODIFY_PICTURE_LASTMODIFIED_FIELD_CHANGED]: (state, action) =>  action.payload + 'T' + state.split('T')[1],
    [ADD_PICTURE_LASTMODIFIED_FIELD_CHANGED]: (state, action) =>  action.payload + 'T' + state.split('T')[1],
    [CLEAR_PICTURE_FORM]: () => initialState,
});

export default albumentryLastModifiedReducer;
