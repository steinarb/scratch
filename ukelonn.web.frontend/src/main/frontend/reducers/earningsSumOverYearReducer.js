import { createReducer } from '@reduxjs/toolkit';
import {
    EARNINGS_SUM_OVER_YEAR_RECEIVE,
    INITIAL_LOGIN_STATE_RECEIVE,
    LOGIN_RECEIVE,
} from '../actiontypes';
import { isAdmin } from '../common/roles';

const accountReducer = createReducer([], {
    [EARNINGS_SUM_OVER_YEAR_RECEIVE]: (state, action) => action.payload,
    [INITIAL_LOGIN_STATE_RECEIVE]: (state, action) => isAdmin(action) ? [] : state,
    [LOGIN_RECEIVE]: (state, action) => isAdmin(action) ? [] : state,
});

export default accountReducer;