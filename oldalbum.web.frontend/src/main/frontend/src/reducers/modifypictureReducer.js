import { createReducer } from '@reduxjs/toolkit';
import {
    ALLROUTES_RECEIVE,
    MODIFY_PICTURE,
    MODIFY_PICTURE_CLEAR,
    MODIFY_PICTURE_PARENT,
    MODIFY_PICTURE_BASENAME,
    MODIFY_PICTURE_TITLE,
    MODIFY_PICTURE_DESCRIPTION,
    MODIFY_PICTURE_IMAGEURL,
    MODIFY_PICTURE_THUMBNAILURL,
    IMAGE_METADATA,
} from '../reduxactions';
import { prepareAlbumentryForEdit, updateParent, updateBasename } from './commonReducerCode';

const modifypictureReducer = createReducer({}, {
    [ALLROUTES_RECEIVE]: (state, action) => prepareAlbumentryForEdit(action.payload.find(i => i.id === state.id)),
    [MODIFY_PICTURE]: (state, action) => prepareAlbumentryForEdit(action.payload),
    [MODIFY_PICTURE_CLEAR]: (state, action) => ({}),
    [MODIFY_PICTURE_PARENT]: (state, action) => updateParent(state, action, false),
    [MODIFY_PICTURE_BASENAME]: (state, action) => updateBasename(state, action, false),
    [MODIFY_PICTURE_TITLE]: (state, action) => ({ ...state, title: action.payload }),
    [MODIFY_PICTURE_DESCRIPTION]: (state, action) => ({ ...state, description: action.payload }),
    [MODIFY_PICTURE_IMAGEURL]: (state, action) => ({ ...state, imageUrl: action.payload }),
    [MODIFY_PICTURE_THUMBNAILURL]: (state, action) => ({ ...state, thumbnailUrl: action.payload }),
    [IMAGE_METADATA]: (state, action) => ({ ...state, ...action.payload, description: state.description || action.payload.description }),
});

export default modifypictureReducer;
