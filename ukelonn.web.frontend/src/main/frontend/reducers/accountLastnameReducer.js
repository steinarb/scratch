import { createReducer } from '@reduxjs/toolkit';
import {
    MODIFY_ACCOUNT_LASTNAME,
    REGISTERPAYMENT_RECEIVE,
    CLEAR_ACCOUNT,
} from '../actiontypes';

const accountLastnameReducer = createReducer('', {
    [MODIFY_ACCOUNT_LASTNAME]: (state, action) => action.payload,
    [REGISTERPAYMENT_RECEIVE]: (state, action) => action.payload.lastName,
    [CLEAR_ACCOUNT]: () => '',
});

export default accountLastnameReducer;
