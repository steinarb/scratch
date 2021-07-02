import { createReducer } from '@reduxjs/toolkit';
import {
    PASSWORD_ENDRE,
    LOGIN_MOTTA,
} from '../actiontypes';

const passwordReducer = createReducer('', {
    [PASSWORD_ENDRE]: (state, action) => action.payload,
    [LOGIN_MOTTA]: (state, action) => action.payload.suksess ? '' : state,
});

export default passwordReducer;
