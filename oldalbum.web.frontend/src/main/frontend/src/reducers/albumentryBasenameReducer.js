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

const albumentryBasenameReducer = createReducer(initialState, builder => {
    builder
        .addCase(FILL_MODIFY_ALBUM_FORM, (state, action) => finnSisteLeddIPath(action, true))
        .addCase(FILL_ADD_ALBUM_FORM, (state, action) => finnSisteLeddIPath(action, true))
        .addCase(FILL_MODIFY_PICTURE_FORM, (state, action) => finnSisteLeddIPath(action, false))
        .addCase(FILL_ADD_PICTURE_FORM, (state, action) => finnSisteLeddIPath(action, false))
        .addCase(MODIFY_ALBUM_BASENAME_FIELD_CHANGED, (state, action) => action.payload)
        .addCase(ADD_ALBUM_BASENAME_FIELD_CHANGED, (state, action) => action.payload)
        .addCase(MODIFY_PICTURE_BASENAME_FIELD_CHANGED, (state, action) => action.payload)
        .addCase(ADD_PICTURE_BASENAME_FIELD_CHANGED, (state, action) => action.payload)
        .addCase(CLEAR_ALBUM_FORM, () => initialState)
        .addCase(CLEAR_PICTURE_FORM, () => initialState);
});

export default albumentryBasenameReducer;

function finnSisteLeddIPath(action, endsWithSlash) {
    const path = action.payload.path;
    return (endsWithSlash ? path.replace(/\/$/, '') : path).split(/\//).pop();
}
