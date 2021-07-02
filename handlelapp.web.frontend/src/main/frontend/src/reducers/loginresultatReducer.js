import { createReducer } from '@reduxjs/toolkit';
import {
    LOGIN_MOTTA,
    LOGOUT_MOTTA,
    LOGINTILSTAND_MOTTA,
} from '../actiontypes';

const loginresultatReducer = createReducer({}, {
    [LOGIN_MOTTA]: (state, action) => action.payload,
    [LOGOUT_MOTTA]: (state, action) => action.payload,
    [LOGINTILSTAND_MOTTA]: (state, action) => action.payload,
});

export default loginresultatReducer;
