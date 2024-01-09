import { createReducer } from '@reduxjs/toolkit';
import {
    OVERSIKT_MOTTA,
    NYHANDLING_LAGRET,
} from '../actiontypes';

const defaultState = {
    oversiktresultat: {},
};

const oversiktReducer = createReducer(defaultState, builder => {
    builder
        .addCase(OVERSIKT_MOTTA, (state, action) => action.payload)
        .addCase(NYHANDLING_LAGRET, (state, action) => action.payload);
});

export default oversiktReducer;
