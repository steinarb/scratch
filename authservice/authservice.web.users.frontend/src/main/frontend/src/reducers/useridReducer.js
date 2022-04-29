import { createReducer } from '@reduxjs/toolkit';
import {
    SELECT_USER,
    USER_CLEAR,
    USERS_RECEIVED,
} from '../actiontypes';

const defaultValue = -1;

const useridReducer = createReducer(defaultValue, {
    [SELECT_USER]: (state, action) => action.payload,
    [USER_CLEAR]: () => defaultValue,
    [USERS_RECEIVED]: () => defaultValue,
});

export default useridReducer;
