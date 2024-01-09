import { createReducer } from '@reduxjs/toolkit';
import {
    SUMYEAR_MOTTA,
} from '../actiontypes';

const sumyearReducer = createReducer([], builder => {
    builder
        .addCase(SUMYEAR_MOTTA, (state, action) => action.payload);
});

export default sumyearReducer;
