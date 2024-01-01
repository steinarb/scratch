import { createReducer } from '@reduxjs/toolkit';
import {
    LOGIN_RECEIVE,
    LOGIN_CHECK_RECEIVE,
    LOGOUT_RECEIVE,
} from '../reduxactions';
const initialState = false;

const canModifyAlbumReducer = createReducer(initialState, builder => {
    builder
        .addCase(LOGIN_RECEIVE, (state, action) => action.payload.canModifyAlbum)
        .addCase(LOGIN_CHECK_RECEIVE, (state, action) => action.payload.canModifyAlbum)
        .addCase(LOGOUT_RECEIVE, (state, action) => action.payload.canModifyAlbum);
});

export default canModifyAlbumReducer;
