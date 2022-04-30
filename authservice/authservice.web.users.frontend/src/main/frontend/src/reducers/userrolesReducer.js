import { createReducer } from '@reduxjs/toolkit';
import {
    USERROLES_RECEIVED,
    USER_ADD_ROLE_RECEIVE,
    USER_REMOVE_ROLE_RECEIVE,
} from '../actiontypes';

const userrolesReducer = createReducer([], {
    [USERROLES_RECEIVED]: (state, action) => action.payload,
    [USER_ADD_ROLE_RECEIVE]: (state, action) => action.payload,
    [USER_REMOVE_ROLE_RECEIVE]: (state, action) => action.payload,
});

export default userrolesReducer;
