import { createReducer } from '@reduxjs/toolkit';
import {
    ADD_ALBUM,
    ADD_ALBUM_CLEAR,
    ADD_ALBUM_BASENAME,
    ADD_ALBUM_TITLE,
    ADD_ALBUM_DESCRIPTION,
} from '../reduxactions';
import { updateBasename } from './commonReducerCode';

const addalbumReducer = createReducer({}, {
    [ADD_ALBUM]: (state, action) => action.payload,
    [ADD_ALBUM_CLEAR]: () => ({}),
    [ADD_ALBUM_BASENAME]: (state, action) => updateBasename(state, action, true),
    [ADD_ALBUM_TITLE]: (state, action) => ({ ...state, title: action.payload }),
    [ADD_ALBUM_DESCRIPTION]: (state, action) => ({ ...state, description: action.payload }),
});

export default addalbumReducer;
