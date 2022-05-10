import { createReducer } from '@reduxjs/toolkit';
import {
    LOGIN_RECEIVE,
    LOGIN_CHECK_RECEIVE,
    LOGOUT_RECEIVE,
} from '../reduxactions';
const initialState = '';

const logstatusMessageReducer = createReducer(initialState, {
    [LOGIN_RECEIVE]: (state, action) => action.payload.errormessage,
    [LOGIN_CHECK_RECEIVE]: (state, action) => action.payload.errormessage,
    [LOGOUT_RECEIVE]: (state, action) => action.payload.errormessage,
});

export default logstatusMessageReducer;
