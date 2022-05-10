import { createReducer } from '@reduxjs/toolkit';
import {
    LOGIN_RECEIVE,
    LOGIN_CHECK_RECEIVE,
    LOGOUT_RECEIVE,
} from '../reduxactions';
const initialState = null;

const usernameReducer = createReducer(initialState, {
    [LOGIN_RECEIVE]: (state, action) => action.payload.username,
    [LOGIN_CHECK_RECEIVE]: (state, action) => action.payload.username,
    [LOGOUT_RECEIVE]: (state, action) => action.payload.username,
});

export default usernameReducer;
