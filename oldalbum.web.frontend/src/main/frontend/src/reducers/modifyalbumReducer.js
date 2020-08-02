import { createReducer } from '@reduxjs/toolkit';
import {
    ALLROUTES_RECEIVE,
    MODIFY_ALBUM,
    MODIFY_ALBUM_CLEAR,
    MODIFY_ALBUM_PARENT,
    MODIFY_ALBUM_BASENAME,
    MODIFY_ALBUM_TITLE,
    MODIFY_ALBUM_DESCRIPTION,
} from '../reduxactions';
import { prepareAlbumentryForEdit, updateParent, updateBasename } from './commonReducerCode';

const modifyalbumReducer = createReducer({}, {
    [ALLROUTES_RECEIVE]: (state, action) => prepareAlbumentryForEdit(action.payload.find(i => i.id === state.id)),
    [MODIFY_ALBUM]: (state, action) => prepareAlbumentryForEdit(action.payload),
    [MODIFY_ALBUM_CLEAR]: (state, action) => ({}),
    [MODIFY_ALBUM_PARENT]: (state, action) => updateParent(state, action, true),
    [MODIFY_ALBUM_BASENAME]: (state, action) => updateBasename(state, action, true),
    [MODIFY_ALBUM_TITLE]: (state, action) => ({ ...state, title: action.payload }),
    [MODIFY_ALBUM_DESCRIPTION]: (state, action) => ({ ...state, description: action.payload }),
});

export default modifyalbumReducer;
