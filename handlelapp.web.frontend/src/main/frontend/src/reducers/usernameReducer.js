import { createReducer } from '@reduxjs/toolkit';
import {
    USERNAME_ENDRE,
} from '../actiontypes';

const usernameReducer = createReducer('', {
    [USERNAME_ENDRE]: (state, action) => action.payload,
});

export default usernameReducer;
