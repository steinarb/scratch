import { createReducer } from '@reduxjs/toolkit';
import {
    HANDLINGER_MOTTA,
    VALGT_BUTIKK,
    HOME_BUTIKKNAVN_ENDRE,
    BUTIKKNAVN_ENDRE,
    NYBUTIKK_LAGRET,
    BUTIKK_LAGRET,
} from '../actiontypes';

const defaultState = '';

const butikkReducer = createReducer(defaultState, builder => {
    builder
        .addCase(HANDLINGER_MOTTA, (state, action) => finnSisteButikknavn(action.payload))
        .addCase(VALGT_BUTIKK, (state, action) => action.payload.butikknavn)
        .addCase(HOME_BUTIKKNAVN_ENDRE, (state, action) => action.payload)
        .addCase(BUTIKKNAVN_ENDRE, (state, action) => action.payload)
        .addCase(NYBUTIKK_LAGRET, () => (defaultState))
        .addCase(BUTIKK_LAGRET, () => (defaultState));
});

export default butikkReducer;

function finnSisteButikknavn(handlinger) {
    const sistebutikk = [...handlinger].pop();
    return sistebutikk.butikk;
}
