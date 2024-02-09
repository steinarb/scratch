import { createReducer } from '@reduxjs/toolkit';
import {
    SET_MODIFY_FAILED_ERROR,
    CLEAR_ALERT,
} from '../reduxactions';

const modifyFailedErrorReducer = createReducer('', (builder) => {
    builder
        .addCase(SET_MODIFY_FAILED_ERROR, (state, action) => action.payload)
        .addCase(CLEAR_ALERT, () => '');
});

export default modifyFailedErrorReducer;
