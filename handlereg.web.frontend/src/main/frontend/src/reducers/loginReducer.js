import { createSlice, createAction } from '@reduxjs/toolkit';

const defaultState = {
    loginresultat: {},
};

const login = createSlice({
    name: 'login',
    initialState: defaultState,
    reducers: {
        USERNAME_ENDRE: (state, action) => {
            const username = action.payload;
            return { ...state, username };
        },
        PASSWORD_ENDRE: (state, action) => {
            const password = action.payload;
            return { ...state, password };
        },
        LOGIN_MOTTA: (state, action) => {
            const loginresultat = action.payload;
            const { suksess } = loginresultat;
            const password = suksess ? '' : state.password;
            return { ...state, loginresultat, password };
        },
    },
});

export const LOGIN_HENT = createAction('LOGIN_HENT');

export default login;
