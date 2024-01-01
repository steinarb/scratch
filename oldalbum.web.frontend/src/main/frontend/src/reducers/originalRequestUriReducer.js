import { createReducer } from '@reduxjs/toolkit';
import {
    LOGIN_RECEIVE,
    LOGIN_CHECK_RECEIVE,
    LOGOUT_RECEIVE,
} from '../reduxactions';
const initialState = null;

const originalRequestUriReducer = createReducer(initialState, (builder) => {
    builder
        .addCase(LOGIN_RECEIVE, (state, action) => action.payload.originalRequestUri)
        .addCase(LOGIN_CHECK_RECEIVE, () => initialState)
        .addCase(LOGOUT_RECEIVE, () => initialState);
});

export default originalRequestUriReducer;
