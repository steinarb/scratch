import { createReducer } from '@reduxjs/toolkit';
import {
    LOGIN_RECEIVE,
    LOGIN_CHECK_RECEIVE,
    LOGOUT_RECEIVE,
} from '../reduxactions';
const initialState = false;

const canLoginReducer = createReducer(initialState, {
    [LOGIN_RECEIVE]: (state, action) => action.payload.canLogin,
    [LOGIN_CHECK_RECEIVE]: (state, action) => action.payload.canLogin,
    [LOGOUT_RECEIVE]: (state, action) => action.payload.canLogin,
});

export default canLoginReducer;
