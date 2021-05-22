import { createReducer } from '@reduxjs/toolkit';
import {
    USERNAME_MODIFY,
    PASSWORD_MODIFY,
    LOGIN_RECEIVE,
    LOGOUT_RECEIVE,
} from '../reduxactions';

const defaultState = {
    loginresult: {},
};

const loginReducer = createReducer(defaultState, {
    [USERNAME_MODIFY]: (state, action) => {
        const username = action.payload;
        return { ...state, username };
    },
    [PASSWORD_MODIFY]: (state, action) => {
        const password = action.payload;
        return { ...state, password };
    },
    [LOGIN_RECEIVE]: (state, action) => {
        const loginresult = action.payload;
        const { success } = loginresult;
        const password = success ? '' : state.password;
        return { ...state, loginresult, password };
    },
    [LOGOUT_RECEIVE]: (state, action) => {
        const loginresult = action.payload;
        return { ...state, loginresult };
    },
});

export default loginReducer;
