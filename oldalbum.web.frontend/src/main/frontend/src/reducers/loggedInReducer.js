import { createReducer } from '@reduxjs/toolkit';
import {
    LOGIN_RECEIVE,
    LOGIN_CHECK_RECEIVE,
    LOGOUT_RECEIVE,
} from '../reduxactions';
const initialState = false;

const loginresultReducer = createReducer(initialState, {
    [LOGIN_RECEIVE]: (state, action) => action.payload.success,
    [LOGIN_CHECK_RECEIVE]: (state, action) => action.payload.success,
    [LOGOUT_RECEIVE]: (state, action) => action.payload.success,
});

export default loginresultReducer;
