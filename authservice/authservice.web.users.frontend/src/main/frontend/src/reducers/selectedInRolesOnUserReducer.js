import { createReducer } from '@reduxjs/toolkit';
import {
    SELECT_ROLES_ON_USER,
    ROLES_ON_USER_CLEAR,
} from '../actiontypes';
import { emptyRole } from '../constants';

const selectedInRolesOnUserReducer = createReducer(emptyRole.id, {
    [SELECT_ROLES_ON_USER]: (state, action) => action.payload,
    [ROLES_ON_USER_CLEAR]: () => emptyRole.id,
});

export default selectedInRolesOnUserReducer;
