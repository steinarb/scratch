import { createReducer } from '@reduxjs/toolkit';
import {
    JOB_TABLE_ROW_CLICK,
    CLEAR_EDIT_JOB_FORM,
} from '../actiontypes';
const unselectedId = -1;

const transactionIdReducer = createReducer(unselectedId, builder => {
    builder
        .addCase(JOB_TABLE_ROW_CLICK, (state, action) => action.payload.id)
        .addCase(CLEAR_EDIT_JOB_FORM, () => unselectedId);
});

export default transactionIdReducer;
