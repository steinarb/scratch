import { createReducer } from '@reduxjs/toolkit';
import {
    SUMBUTIKK_MOTTA,
} from '../actiontypes';

const sumbutikkReducer = createReducer([], builder => {
    builder
        .addCase(SUMBUTIKK_MOTTA, (state, action) => action.payload);
});

export default sumbutikkReducer;
