import { createReducer } from '@reduxjs/toolkit';
import {
    MODIFY_ACCOUNT_FIRSTNAME,
    REGISTERPAYMENT_RECEIVE,
    CLEAR_ACCOUNT,
} from '../actiontypes';

const accountFirstnameReducer = createReducer('', {
    [MODIFY_ACCOUNT_FIRSTNAME]: (state, action) => action.payload,
    [REGISTERPAYMENT_RECEIVE]: (state, action) => action.payload.firstName,
    [CLEAR_ACCOUNT]: () => '',
});

export default accountFirstnameReducer;
