import { createReducer } from '@reduxjs/toolkit';
import {
    FILL_MODIFY_ALBUM_FORM,
    FILL_ADD_ALBUM_FORM,
    FILL_MODIFY_PICTURE_FORM,
    FILL_ADD_PICTURE_FORM,
    MODIFY_ALBUM_PARENT_SELECTED,
    MODIFY_ALBUM_BASENAME_FIELD_CHANGED,
    ADD_ALBUM_BASENAME_FIELD_CHANGED,
    MODIFY_PICTURE_PARENT_SELECTED,
    MODIFY_PICTURE_BASENAME_FIELD_CHANGED,
    ADD_PICTURE_BASENAME_FIELD_CHANGED,
    CLEAR_ALBUM_FORM,
    CLEAR_PICTURE_FORM,
} from '../reduxactions';
const initialState = '';

const albumentryPathReducer = createReducer(initialState, builder => {
    builder
        .addCase(FILL_MODIFY_ALBUM_FORM, (state, action) => action.payload.path)
        .addCase(FILL_ADD_ALBUM_FORM, (state, action) => action.payload.path || state)
        .addCase(FILL_MODIFY_PICTURE_FORM, (state, action) => action.payload.path)
        .addCase(FILL_ADD_PICTURE_FORM, (state, action) => action.payload.path)
        .addCase(MODIFY_ALBUM_PARENT_SELECTED, (state, action) => replaceParentPath(state, action.payload, true))
        .addCase(MODIFY_PICTURE_PARENT_SELECTED, (state, action) => replaceParentPath(state, action.payload, false))
        .addCase(MODIFY_ALBUM_BASENAME_FIELD_CHANGED, (state, action) => replaceLastElementInPathWithBasename(state, action.payload, true))
        .addCase(ADD_ALBUM_BASENAME_FIELD_CHANGED, (state, action) => replaceLastElementInPathWithBasename(state, action.payload, true))
        .addCase(MODIFY_PICTURE_BASENAME_FIELD_CHANGED, (state, action) => replaceLastElementInPathWithBasename(state, action.payload, false))
        .addCase(ADD_PICTURE_BASENAME_FIELD_CHANGED, (state, action) => replaceLastElementInPathWithBasename(state, action.payload, false))
        .addCase(CLEAR_ALBUM_FORM, () => initialState)
        .addCase(CLEAR_PICTURE_FORM, () => initialState);
});

export default albumentryPathReducer;

function replaceParentPath(state, parent, endsWithSlash) {
    const basename = state.replace(/\/$/, '').split(/\//).pop();
    const pathElements = parent.path.replace(/\/$/, '').split(/\//);
    pathElements.push(basename);
    return pathElements.join('/') + (endsWithSlash ? '/' : '');
}

function replaceLastElementInPathWithBasename(state, basename, endsWithSlash) {
    const pathElements = (endsWithSlash ? state.replace(/\/$/, '') : state).split(/\//);
    pathElements.pop();
    pathElements.push(basename);
    return pathElements.join('/') + (endsWithSlash ? '/' : '');
}
