import { createReducer } from '@reduxjs/toolkit';
import {
    ALLROUTES_RECEIVE,
    ADD_PICTURE,
    ADD_PICTURE_CLEAR,
    ADD_PICTURE_BASENAME,
    ADD_PICTURE_TITLE,
    ADD_PICTURE_DESCRIPTION,
    ADD_PICTURE_IMAGEURL,
    ADD_PICTURE_THUMBNAILURL,
} from '../reduxactions';
import { updateParent, updateBasename } from './commonReducerCode';

const addpictureReducer = createReducer({}, {
    [ADD_PICTURE]: (state, action) => action.payload,
    [ADD_PICTURE_CLEAR]: (state, action) => ({}),
    [ADD_PICTURE_BASENAME]: (state, action) => updateBasename(state, action, false),
    [ADD_PICTURE_TITLE]: (state, action) => ({ ...state, title: action.payload }),
    [ADD_PICTURE_DESCRIPTION]: (state, action) => ({ ...state, description: action.payload }),
    [ADD_PICTURE_IMAGEURL]: (state, action) => setImageUrlAndSetBasenameIfEmpty(state, action),
    [ADD_PICTURE_THUMBNAILURL]: (state, action) => setThumbnailUrlAndSetBasenameIfEmpty(state, action),
});

export default addpictureReducer;

function setImageUrlAndSetBasenameIfEmpty(state, action) {
    const { imageUrl, parentalbum } = action.payload;
    const basename = state.basename ? state.basename : basenameOfUrl(imageUrl);
    const path = parentalbum.path + basename;
    return { ...state, path, basename, imageUrl };
}

function setThumbnailUrlAndSetBasenameIfEmpty(state, action) {
    const { thumbnailUrl, parentalbum } = action.payload;
    const basename = state.basename ? state.basename : basenameOfUrl(thumbnailUrl);
    const path = parentalbum.path + basename;
    return { ...state, path, basename, thumbnailUrl };
}

function basenameOfUrl(url) {
    if (!url) { return url; }

    return url.split(/\//).pop().split(/\./).shift();
}
