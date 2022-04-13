import { createReducer } from '@reduxjs/toolkit';
import {
    SELECT_USER,
    CLEAR_USER,
} from '../actiontypes';

const userIdReducer = createReducer(-1, {
    [SELECT_USER]: (state, action) => action.payload,
    [CLEAR_USER]: () => -1,
});

export default userIdReducer;
