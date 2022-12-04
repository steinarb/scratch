import { createReducer } from '@reduxjs/toolkit';
import {
    LOGIN_RECEIVE,
    LOGIN_CHECK_RECEIVE,
    LOGOUT_RECEIVE,
} from '../reduxactions';
const initialState = null;

const originalRequestUriReducer = createReducer(initialState, {
    [LOGIN_RECEIVE]: (state, action) => action.payload.originalRequestUri,
    [LOGIN_CHECK_RECEIVE]: () => initialState,
    [LOGOUT_RECEIVE]: () => initialState,
});

export default originalRequestUriReducer;
