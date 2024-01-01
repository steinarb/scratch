import { createReducer } from '@reduxjs/toolkit';
import {
    LOGIN_RECEIVE,
    LOGIN_CHECK_RECEIVE,
    LOGOUT_RECEIVE,
} from '../reduxactions';
const initialState = '';

const logstatusMessageReducer = createReducer(initialState, (builder) => {
    builder
        .addCase(LOGIN_RECEIVE, (state, action) => action.payload.errormessage)
        .addCase(LOGIN_CHECK_RECEIVE, (state, action) => action.payload.errormessage)
        .addCase(LOGOUT_RECEIVE, (state, action) => action.payload.errormessage);
});

export default logstatusMessageReducer;
