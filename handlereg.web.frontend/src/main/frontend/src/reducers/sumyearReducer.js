import { createReducer } from '@reduxjs/toolkit';
import {
    SUMYEAR_MOTTA,
} from '../actiontypes';

const sumyearReducer = createReducer([], {
    [SUMYEAR_MOTTA]: (state, action) => action.payload,
});

export default sumyearReducer;
