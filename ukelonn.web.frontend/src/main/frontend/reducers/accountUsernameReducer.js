import { createReducer } from '@reduxjs/toolkit';
import {
    MODIFY_ACCOUNT_USERNAME,
    REGISTERPAYMENT_RECEIVE,
    CLEAR_ACCOUNT,
} from '../actiontypes';

const accountUsernameReducer = createReducer('', {
    [MODIFY_ACCOUNT_USERNAME]: (state, action) => action.payload,
    [REGISTERPAYMENT_RECEIVE]: (state, action) => action.payload.username,
    [CLEAR_ACCOUNT]: () => '',
});

export default accountUsernameReducer;
