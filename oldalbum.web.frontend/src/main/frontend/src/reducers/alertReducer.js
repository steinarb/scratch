import { createReducer } from '@reduxjs/toolkit';
import {
    SET_ALERT,
    CLEAR_ALERT,
} from '../reduxactions';

const alertReducer = createReducer('', (builder) => {
    builder
        .addCase(SET_ALERT, (state, action) => action.payload)
        .addCase(CLEAR_ALERT, () => '');
});

export default alertReducer;
