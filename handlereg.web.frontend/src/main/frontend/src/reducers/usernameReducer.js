import { createReducer } from '@reduxjs/toolkit';
import {
    USERNAME_ENDRE,
    OVERSIKT_MOTTA,
} from '../actiontypes';

const usernameReducer = createReducer('', {
    [USERNAME_ENDRE]: (state, action) => action.payload,
    [OVERSIKT_MOTTA]: (state, action) => action.payload.brukernavn,
});

export default usernameReducer;
