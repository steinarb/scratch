import { createReducer } from '@reduxjs/toolkit';
import {
    MODIFY_JOB_AMOUNT,
    MODIFY_PAYMENT_AMOUNT,
    MODIFY_ACCOUNT_BALANCE,
    REGISTERJOB_RECEIVE,
    REGISTERPAYMENT_RECEIVE,
    JOB_TABLE_ROW_CLICK,
    CLEAR_JOB_FORM,
    CLEAR_JOB_TYPE_CREATE_FORM,
    CLEAR_PAYMENT_TYPE_FORM,
    CLEAR_ACCOUNT,
} from '../actiontypes';
const emptyAmount = 0;

const transactionAmountReducer = createReducer(emptyAmount, {
    [MODIFY_JOB_AMOUNT]: (state, action) => action.payload,
    [MODIFY_PAYMENT_AMOUNT]: (state, action) => action.payload,
    [MODIFY_ACCOUNT_BALANCE]: (state, action) => action.payload,
    [REGISTERJOB_RECEIVE]: () => emptyAmount,
    [REGISTERPAYMENT_RECEIVE]: (state, action) => action.payload.balance,
    [JOB_TABLE_ROW_CLICK]: (state, action) => parseInt(action.payload.transactionAmount),
    [CLEAR_JOB_FORM]: () => emptyAmount,
    [CLEAR_JOB_TYPE_CREATE_FORM]: () => emptyAmount,
    [CLEAR_PAYMENT_TYPE_FORM]: () => emptyAmount,
    [CLEAR_ACCOUNT]: () => emptyAmount,
});

export default transactionAmountReducer;
