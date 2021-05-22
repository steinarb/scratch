import { createReducer } from '@reduxjs/toolkit';
import {
    LOGIN_RECEIVE,
} from '../reduxactions';

const haveReceivedInitialLoginStatusReducer = createReducer(false, {
    [LOGIN_RECEIVE]: () => true,
});

export default haveReceivedInitialLoginStatusReducer;
