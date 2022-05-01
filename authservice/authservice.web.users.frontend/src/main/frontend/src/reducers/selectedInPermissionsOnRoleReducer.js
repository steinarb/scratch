import { createReducer } from '@reduxjs/toolkit';
import {
    PERMISSIONS_ON_ROLE_SELECT,
    PERMISSIONS_ON_ROLE_CLEAR,
} from '../actiontypes';
import { emptyPermission } from '../constants';

const selectedInPermissionsOnRoleReducer = createReducer(emptyPermission.id, {
    [PERMISSIONS_ON_ROLE_SELECT]: (state, action) => action.payload,
    [PERMISSIONS_ON_ROLE_CLEAR]: () => emptyPermission.id,
});

export default selectedInPermissionsOnRoleReducer;
