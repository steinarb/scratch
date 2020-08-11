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
    [ADD_PICTURE_IMAGEURL]: (state, action) => ({ ...state, imageUrl: action.payload }),
    [ADD_PICTURE_THUMBNAILURL]: (state, action) => ({ ...state, thumbnailUrl: action.payload }),
});

export default addpictureReducer;
