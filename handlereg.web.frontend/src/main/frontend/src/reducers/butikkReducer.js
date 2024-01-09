import { createReducer } from '@reduxjs/toolkit';
import {
    VALGT_BUTIKK,
    NYBUTIKK_LAGRET,
    BUTIKK_LAGRET,
} from '../actiontypes';

const defaultState = {
    storeId: -1,
    butikknavn: '',
    gruppe: 2,
};

const butikkReducer = createReducer(defaultState, builder => {
    builder
        .addCase(VALGT_BUTIKK, (state, action) => action.payload)
        .addCase(NYBUTIKK_LAGRET, () => ({ ...defaultState }))
        .addCase(BUTIKK_LAGRET, () => ({ ...defaultState }));
});

export default butikkReducer;
