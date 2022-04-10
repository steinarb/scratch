import { createReducer } from '@reduxjs/toolkit';
import {
    MODIFY_ACCOUNT_FULLNAME,
    REGISTERPAYMENT_RECEIVE,
    CLEAR_ACCOUNT,
} from '../actiontypes';

const accountFullnameReducer = createReducer('', {
    [MODIFY_ACCOUNT_FULLNAME]: (state, action) => action.payload,
    [REGISTERPAYMENT_RECEIVE]: (state, action) => action.payload.fullName,
    [CLEAR_ACCOUNT]: () => '',
});

export default accountFullnameReducer;
