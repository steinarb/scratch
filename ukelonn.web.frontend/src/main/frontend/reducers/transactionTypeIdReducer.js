import { createReducer } from '@reduxjs/toolkit';
import {
    SELECT_JOB_TYPE,
    SELECT_PAYMENT_TYPE,
    JOB_TABLE_ROW_CLICK,
    REGISTERJOB_RECEIVE,
    CLEAR_JOB_FORM,
} from '../actiontypes';
const unselectedId = -1;

const transactionTypeIdReducer = createReducer(unselectedId, {
    [SELECT_JOB_TYPE]: (state, action) => action.payload,
    [SELECT_PAYMENT_TYPE]: (state, action) => action.payload,
    [JOB_TABLE_ROW_CLICK]: (state, action) => parseInt(action.payload.transactionType.id),
    [REGISTERJOB_RECEIVE]: () => unselectedId,
    [CLEAR_JOB_FORM]: () => unselectedId,
});

export default transactionTypeIdReducer;
