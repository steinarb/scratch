import { createReducer } from '@reduxjs/toolkit';
import {
    LOGIN_MOTTA,
    LOGOUT_MOTTA,
    LOGINTILSTAND_MOTTA,
} from '../actiontypes';

const loginresultatReducer = createReducer({ authorized: true }, builder => {
    builder
        .addCase(LOGIN_MOTTA, (state, action) => action.payload)
        .addCase(LOGOUT_MOTTA, (state, action) => action.payload)
        .addCase(LOGINTILSTAND_MOTTA, (state, action) => action.payload);
});

export default loginresultatReducer;
