import { createReducer } from '@reduxjs/toolkit';
import {
    LOGIN_RECEIVE,
    LOGIN_CHECK_RECEIVE,
    LOGOUT_RECEIVE,
} from '../reduxactions';
const initialState = false;

const canLoginReducer = createReducer(initialState, builder => {
    builder
        .addCase(LOGIN_RECEIVE, (state, action) => action.payload.canLogin)
        .addCase(LOGIN_CHECK_RECEIVE, (state, action) => action.payload.canLogin)
        .addCase(LOGOUT_RECEIVE, (state, action) => action.payload.canLogin);
});

export default canLoginReducer;
