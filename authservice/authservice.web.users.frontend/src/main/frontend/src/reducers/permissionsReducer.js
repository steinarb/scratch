import { createReducer } from '@reduxjs/toolkit';
import {
    PERMISSIONS_RECEIVED,
} from '../actiontypes';

const permissionsReducer = createReducer([], {
    [ PERMISSIONS_RECEIVED]: (state, action) => action.payload,
});

export default permissionsReducer;
