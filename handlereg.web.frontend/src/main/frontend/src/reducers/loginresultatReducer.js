import { createReducer } from '@reduxjs/toolkit';
import {
    LOGIN_MOTTA,
} from '../actiontypes';

const loginresultatReducer = createReducer({}, {
    [LOGIN_MOTTA]: (state, action) => action.payload,
});

export default loginresultatReducer;
