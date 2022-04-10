import { createReducer } from '@reduxjs/toolkit';
import {
    MODIFY_ACCOUNT_BALANCE,
    REGISTERPAYMENT_RECEIVE,
    CLEAR_ACCOUNT,
} from '../actiontypes';

const accountBalanceReducer = createReducer(0, {
    [MODIFY_ACCOUNT_BALANCE]: (state, action) => action.payload,
    [REGISTERPAYMENT_RECEIVE]: (state, action) => action.payload.balance,
    [CLEAR_ACCOUNT]: () => 0,
});

export default accountBalanceReducer;
