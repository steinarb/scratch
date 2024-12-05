import { createReducer } from '@reduxjs/toolkit';
import { VELG_BUTIKK } from '../actiontypes';
import { api } from '../api';

const defaultState = {
    storeId: -1,
    butikknavn: '',
    gruppe: 2,
};

const butikkReducer = createReducer(defaultState, builder => {
    builder
        .addCase(VELG_BUTIKK, (state, action) => action.payload)
        .addMatcher(api.endpoints.postNybutikk.matchFulfilled, () => ({ ...defaultState }))
        .addMatcher(api.endpoints.postEndrebutikk.matchFulfilled, () => ({ ...defaultState }));
});

export default butikkReducer;
