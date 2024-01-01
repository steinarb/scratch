import { createReducer } from '@reduxjs/toolkit';
import {
    LOGIN_RECEIVE,
    LOGIN_CHECK_RECEIVE,
    LOGOUT_RECEIVE,
} from '../reduxactions';
const initialState = false;

const loginresultReducer = createReducer(initialState, (builder) => {
    builder
        .addCase(LOGIN_RECEIVE, (state, action) => action.payload.success)
        .addCase(LOGIN_CHECK_RECEIVE, (state, action) => action.payload.success)
        .addCase(LOGOUT_RECEIVE, (state, action) => action.payload.success);
});

export default loginresultReducer;
