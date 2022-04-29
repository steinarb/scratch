import { createReducer } from '@reduxjs/toolkit';
import {
    SELECTED_USER,
    MODIFY_LASTNAME,
    USER_CLEAR,
    SAVE_MODIFIED_USER_RECEIVE,
    SAVE_PASSWORDS_MODIFY_RECEIVE,
} from '../actiontypes';
import { isUnselected } from './common';

const defaultValue = '';

const lastnameReducer = createReducer(defaultValue, {
    [SELECTED_USER]: (state, action) => isUnselected(action.payload.userid) ? defaultValue : action.payload.lastname,
    [MODIFY_LASTNAME]: (state, action) => action.payload,
    [USER_CLEAR]: () => defaultValue,
    [SAVE_MODIFIED_USER_RECEIVE]: () => defaultValue,
    [SAVE_PASSWORDS_MODIFY_RECEIVE]: () => defaultValue,
});

export default lastnameReducer;
