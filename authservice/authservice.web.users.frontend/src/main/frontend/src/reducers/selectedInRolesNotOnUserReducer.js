import { createReducer } from '@reduxjs/toolkit';
import {
    SELECT_ROLES_NOT_ON_USER,
    ROLES_NOT_ON_USER_CLEAR,
} from '../actiontypes';
import { emptyRole } from '../constants';

const selectedInRolesNotOnUserReducer = createReducer(emptyRole.id, {
    [SELECT_ROLES_NOT_ON_USER]: (state, action) => action.payload,
    [ROLES_NOT_ON_USER_CLEAR]: () => emptyRole.id,
});

export default selectedInRolesNotOnUserReducer;
