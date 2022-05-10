import { createReducer } from '@reduxjs/toolkit';
import {
    LOGIN_RECEIVE,
    LOGIN_CHECK_RECEIVE,
    LOGOUT_RECEIVE,
} from '../reduxactions';
const initialState = false;

const canModifyAlbumReducer = createReducer(initialState, {
    [LOGIN_RECEIVE]: (state, action) => action.payload.canModifyAlbum,
    [LOGIN_CHECK_RECEIVE]: (state, action) => action.payload.canModifyAlbum,
    [LOGOUT_RECEIVE]: (state, action) => action.payload.canModifyAlbum,
});

export default canModifyAlbumReducer;
