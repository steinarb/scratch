import { createReducer } from '@reduxjs/toolkit';
import {
    USERNAME_ENDRE,
    PASSWORD_ENDRE,
    LOGIN_MOTTA,
} from '../actiontypes';

const defaultState = {
    loginresultat: {},
};

const loginReducer = createReducer(defaultState, {
    [USERNAME_ENDRE]: (state, action) => {
        const username = action.payload;
        return { ...state, username };
    },
    [PASSWORD_ENDRE]: (state, action) => {
        const password = action.payload;
        return { ...state, password };
    },
    [LOGIN_MOTTA]: (state, action) => {
        const loginresultat = action.payload;
        const { suksess } = loginresultat;
        const password = suksess ? '' : state.password;
        return { ...state, loginresultat, password };
    },
});

export default loginReducer;
