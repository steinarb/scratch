import { createReducer } from '@reduxjs/toolkit';
import {
    SET_ALERT,
    CLEAR_ALERT,
} from '../reduxactions';

const alertReducer = createReducer('', {
    [SET_ALERT]: (state, action) => action.payload,
    [CLEAR_ALERT]: (state, action) => '',
});

export default alertReducer;
