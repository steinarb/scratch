import { createReducer } from '@reduxjs/toolkit';
import {
    SELECT_ACCOUNT,
    MODIFY_ACCOUNT_ID,
    REGISTERPAYMENT_RECEIVE,
    CLEAR_ACCOUNT,
} from '../actiontypes';

const accountIdReducer = createReducer(-1, {
    [SELECT_ACCOUNT]: (state, action) => parseInt(action.payload),
    [MODIFY_ACCOUNT_ID]: (state, action) => parseInt(action.payload),
    [REGISTERPAYMENT_RECEIVE]: (state, action) => action.payload.accountId,
    [CLEAR_ACCOUNT]: () => -1,
});

export default accountIdReducer;
