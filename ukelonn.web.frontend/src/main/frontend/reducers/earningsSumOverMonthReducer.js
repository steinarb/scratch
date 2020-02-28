import { createReducer } from '@reduxjs/toolkit';
import {
    EARNINGS_SUM_OVER_MONTH_RECEIVE,
} from '../actiontypes';

const accountReducer = createReducer([], {
    [EARNINGS_SUM_OVER_MONTH_RECEIVE]: (state, action) => action.payload,
});

export default accountReducer;
