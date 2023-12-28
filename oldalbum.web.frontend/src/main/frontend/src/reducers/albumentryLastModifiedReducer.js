import { createReducer } from '@reduxjs/toolkit';
import {
    FILL_MODIFY_ALBUM_FORM,
    FILL_MODIFY_PICTURE_FORM,
    FILL_ADD_PICTURE_FORM,
    IMAGE_METADATA_RECEIVE,
    MODIFY_ALBUM_LASTMODIFIED_FIELD_CHANGED,
    MODIFY_ALBUM_SET_LASTMODIFIED_FIELD_TO_CURRENT_DATE,
    MODIFY_ALBUM_CLEAR_LASTMODIFIED_FIELD,
    MODIFY_PICTURE_LASTMODIFIED_FIELD_CHANGED,
    ADD_ALBUM_LASTMODIFIED_FIELD_CHANGED,
    ADD_ALBUM_SET_LASTMODIFIED_FIELD_TO_CURRENT_DATE,
    ADD_ALBUM_CLEAR_LASTMODIFIED_FIELD,
    ADD_PICTURE_LASTMODIFIED_FIELD_CHANGED,
    CLEAR_ALBUM_FORM,
    CLEAR_PICTURE_FORM,
} from '../reduxactions';
const initialState = '';

const albumentryLastModifiedReducer = createReducer(initialState, {
    [FILL_MODIFY_ALBUM_FORM]: (state, action) => action.payload.lastModified ? new Date(action.payload.lastModified).toISOString() : initialState,
    [FILL_MODIFY_PICTURE_FORM]: (state, action) => new Date(action.payload.lastModified).toISOString(),
    [FILL_ADD_PICTURE_FORM]: (state, action) => action.payload.lastModified,
    [IMAGE_METADATA_RECEIVE]: (state, action) => new Date(action.payload.lastModified).toISOString(),
    [MODIFY_ALBUM_LASTMODIFIED_FIELD_CHANGED]: (state, action) =>  action.payload + 'T' + state.split('T')[1],
    [MODIFY_ALBUM_SET_LASTMODIFIED_FIELD_TO_CURRENT_DATE]: () => new Date().toISOString(),
    [MODIFY_ALBUM_CLEAR_LASTMODIFIED_FIELD]: () => initialState,
    [MODIFY_PICTURE_LASTMODIFIED_FIELD_CHANGED]: (state, action) =>  action.payload + 'T' + state.split('T')[1],
    [ADD_ALBUM_LASTMODIFIED_FIELD_CHANGED]: (state, action) =>  action.payload + 'T' + state.split('T')[1],
    [ADD_ALBUM_SET_LASTMODIFIED_FIELD_TO_CURRENT_DATE]: () => new Date().toISOString(),
    [ADD_ALBUM_CLEAR_LASTMODIFIED_FIELD]: () => initialState,
    [ADD_PICTURE_LASTMODIFIED_FIELD_CHANGED]: (state, action) =>  action.payload + 'T' + state.split('T')[1],
    [CLEAR_ALBUM_FORM]: () => initialState,
    [CLEAR_PICTURE_FORM]: () => initialState,
});

export default albumentryLastModifiedReducer;
