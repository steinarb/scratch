import { createReducer } from '@reduxjs/toolkit';
import {
    USERNAME_MODIFY,
    LOGIN_RECEIVE,
    LOGOUT_RECEIVE,
} from '../reduxactions';

const usernameReducer = createReducer('', {
    [USERNAME_MODIFY]: (state, action) => action.payload,
    [LOGIN_RECEIVE]: (state, action) => action.payload.success ? action.payload.username : state,
    [LOGOUT_RECEIVE]: (state, action) => action.payload.username || '',
});

export default usernameReducer;
