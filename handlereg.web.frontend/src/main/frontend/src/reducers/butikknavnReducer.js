import { createReducer } from '@reduxjs/toolkit';
import {
    VELG_BUTIKK,
    HOME_BUTIKKNAVN_ENDRE,
    BUTIKKNAVN_ENDRE,
} from '../actiontypes';
import { api } from '../api';

const defaultState = '';

const butikkReducer = createReducer(defaultState, builder => {
    builder
        .addMatcher(api.endpoints.getHandlinger.matchFulfilled, (state, action) => finnSisteButikknavn(action.payload))
        .addCase(VELG_BUTIKK, (state, action) => action.payload.butikknavn)
        .addCase(HOME_BUTIKKNAVN_ENDRE, (state, action) => action.payload.navn)
        .addCase(BUTIKKNAVN_ENDRE, (state, action) => action.payload)
        .addMatcher(api.endpoints.postNybutikk.matchFulfilled, () => (defaultState))
        .addMatcher(api.endpoints.postEndrebutikk.matchFulfilled, () => (defaultState));
});

export default butikkReducer;

function finnSisteButikknavn(handlinger) {
    const sistebutikk = [...handlinger].pop();
    return sistebutikk.butikk;
}
