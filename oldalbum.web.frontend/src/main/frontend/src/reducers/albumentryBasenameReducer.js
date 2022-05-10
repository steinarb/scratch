import { createReducer } from '@reduxjs/toolkit';
import {
    FILL_MODIFY_ALBUM_FORM,
    FILL_ADD_ALBUM_FORM,
    FILL_MODIFY_PICTURE_FORM,
    FILL_ADD_PICTURE_FORM,
    MODIFY_ALBUM_BASENAME_FIELD_CHANGED,
    ADD_ALBUM_BASENAME_FIELD_CHANGED,
    MODIFY_PICTURE_BASENAME_FIELD_CHANGED,
    ADD_PICTURE_BASENAME_FIELD_CHANGED,
    CLEAR_ALBUM_FORM,
    CLEAR_PICTURE_FORM,
} from '../reduxactions';
const initialState = '';

const albumentryBasenameReducer = createReducer(initialState, {
    [FILL_MODIFY_ALBUM_FORM]: (state, action) => finnSisteLeddIPath(action, true),
    [FILL_ADD_ALBUM_FORM]: (state, action) => finnSisteLeddIPath(action, true),
    [FILL_MODIFY_PICTURE_FORM]: (state, action) => finnSisteLeddIPath(action, false),
    [FILL_ADD_PICTURE_FORM]: (state, action) => finnSisteLeddIPath(action, false),
    [MODIFY_ALBUM_BASENAME_FIELD_CHANGED]: (state, action) => action.payload,
    [ADD_ALBUM_BASENAME_FIELD_CHANGED]: (state, action) => action.payload,
    [MODIFY_PICTURE_BASENAME_FIELD_CHANGED]: (state, action) => action.payload,
    [ADD_PICTURE_BASENAME_FIELD_CHANGED]: (state, action) => action.payload,
    [CLEAR_ALBUM_FORM]: () => initialState,
    [CLEAR_PICTURE_FORM]: () => initialState,
});

export default albumentryBasenameReducer;

function finnSisteLeddIPath(action, endsWithSlash) {
    const path = action.payload.path;
    return (endsWithSlash ? path.replace(/\/$/, '') : path).split(/\//).pop();
}
