import { createReducer } from '@reduxjs/toolkit';
import {
    LOGIN_RECEIVE,
    LOGIN_CHECK_RECEIVE,
    LOGOUT_RECEIVE,
} from '../reduxactions';
const initialState = null;

const usernameReducer = createReducer(initialState, (builder) => {
    builder
        .addCase(LOGIN_RECEIVE, (state, action) => action.payload.username)
        .addCase(LOGIN_CHECK_RECEIVE, (state, action) => action.payload.username)
        .addCase(LOGOUT_RECEIVE, (state, action) => action.payload.username);
});

export default usernameReducer;
