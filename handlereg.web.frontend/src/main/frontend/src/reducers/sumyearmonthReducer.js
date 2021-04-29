import { createReducer } from '@reduxjs/toolkit';
import {
    SUMYEARMONTH_MOTTA,
} from '../actiontypes';

const sumyearmonthReducer = createReducer([], {
    [SUMYEARMONTH_MOTTA]: (state, action) => action.payload,
});

export default sumyearmonthReducer;
