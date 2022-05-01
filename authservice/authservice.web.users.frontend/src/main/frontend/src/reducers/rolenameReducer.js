import { createReducer } from '@reduxjs/toolkit';
import {
    SELECTED_ROLE,
    MODIFY_ROLENAME,
    ROLE_CLEAR,
    SAVE_MODIFIED_ROLE_RECEIVE,
    SAVE_PASSWORDS_MODIFY_RECEIVE,
    SAVE_ADDED_ROLE_RECEIVE,
} from '../actiontypes';
import { isUnselected } from './common';

const defaultValue = '';

const rolenameReducer = createReducer(defaultValue, {
    [SELECTED_ROLE]: (state, action) => isUnselected(action.payload.id) ? defaultValue : action.payload.rolename,
    [MODIFY_ROLENAME]: (state, action) => action.payload,
    [ROLE_CLEAR]: () => defaultValue,
    [SAVE_MODIFIED_ROLE_RECEIVE]: () => defaultValue,
    [SAVE_PASSWORDS_MODIFY_RECEIVE]: () => defaultValue,
    [SAVE_ADDED_ROLE_RECEIVE]: () => defaultValue,
});

export default rolenameReducer;
