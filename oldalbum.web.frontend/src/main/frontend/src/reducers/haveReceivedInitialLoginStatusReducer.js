import { createReducer } from '@reduxjs/toolkit';
import {
    LOGIN_CHECK_RECEIVE,
} from '../reduxactions';

const haveReceivedInitialLoginStatusReducer = createReducer(false, builder => {
    builder
        .addCase(LOGIN_CHECK_RECEIVE, () => true);
});

export default haveReceivedInitialLoginStatusReducer;
