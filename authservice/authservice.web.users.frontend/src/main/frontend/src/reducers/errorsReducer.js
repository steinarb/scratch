import { createReducer } from '@reduxjs/toolkit';
import {
    USERS_FAILURE,
    USERROLES_ERROR,
    ROLES_ERROR,
    ADD_PERMISSON_TO_ROLE_FAILURE,
    REMOVE_PERMISSON_FROM_ROLE_FAILURE,
    PERMISSIONS_ERROR,
} from '../actiontypes';

const errorsReducer = createReducer({}, {
    [USERS_FAILURE]: (state, action) => {
        const users = action.payload;
        return { ...state, users };
    },
    [USERROLES_ERROR]: (state, action) => {
        const userroles = action.payload;
        return { ...state, userroles };
    },
    [ROLES_ERROR]: (state, action) => {
        const roles = action.payload;
        return { ...state, roles };
    },
    [ADD_PERMISSON_TO_ROLE_FAILURE]: (state, action) => {
        const rolepermissions = action.payload;
        return { ...state, rolepermissions };
    },
    [REMOVE_PERMISSON_FROM_ROLE_FAILURE]: (state, action) => {
        const rolepermissions = action.payload;
        return { ...state, rolepermissions };
    },
    [PERMISSIONS_ERROR]: (state, action) => {
        const permissions = action.payload;
        return { ...state, permissions };
    },
});

export default errorsReducer;
