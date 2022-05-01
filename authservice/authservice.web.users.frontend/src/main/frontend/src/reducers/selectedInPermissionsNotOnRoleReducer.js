import { createReducer } from '@reduxjs/toolkit';
import {
    PERMISSIONS_NOT_ON_ROLE_SELECT,
    PERMISSIONS_NOT_ON_ROLE_CLEAR,
} from '../actiontypes';
import { emptyPermission } from '../constants';

const selectedInPermissionsNotOnRoleReducer = createReducer(emptyPermission.id, {
    [PERMISSIONS_NOT_ON_ROLE_SELECT]: (state, action) => action.payload,
    [PERMISSIONS_NOT_ON_ROLE_CLEAR]: () => emptyPermission.id,
});

export default selectedInPermissionsNotOnRoleReducer;
