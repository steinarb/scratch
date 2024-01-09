import { createReducer } from '@reduxjs/toolkit';
import {
    SUMYEARMONTH_MOTTA,
} from '../actiontypes';

const sumyearmonthReducer = createReducer([], builder => {
    builder
        .addCase(SUMYEARMONTH_MOTTA, (state, action) => action.payload);
});

export default sumyearmonthReducer;
