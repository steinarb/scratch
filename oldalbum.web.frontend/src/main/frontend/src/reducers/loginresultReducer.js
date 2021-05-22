import { createReducer } from '@reduxjs/toolkit';
import {
    LOGIN_RECEIVE,
    LOGOUT_RECEIVE,
} from '../reduxactions';

const defaultState = {
    success: false,
    canModifyAlbum: false,
};

const loginresultReducer = createReducer(defaultState, {
    [LOGIN_RECEIVE]: (state, action) => action.payload,
    [LOGOUT_RECEIVE]: (state, action) => action.payload,
});

export default loginresultReducer;
