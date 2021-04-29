import { createReducer } from '@reduxjs/toolkit';
import {
    SUMBUTIKK_MOTTA,
} from '../actiontypes';

const sumbutikkReducer = createReducer([], {
    [SUMBUTIKK_MOTTA]: (state, action) => action.payload,
});

export default sumbutikkReducer;
