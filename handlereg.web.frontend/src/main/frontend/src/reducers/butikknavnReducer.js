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

const butikkReducer = createReducer(defaultState, {
    [HANDLINGER_MOTTA]: (state, action) => finnSisteButikknavn(action.payload),
    [VALGT_BUTIKK]: (state, action) => action.payload.butikknavn,
    [HOME_BUTIKKNAVN_ENDRE]: (state, action) => action.payload,
    [BUTIKKNAVN_ENDRE]: (state, action) => action.payload,
    [NYBUTIKK_LAGRET]: () => (defaultState),
    [BUTIKK_LAGRET]: () => (defaultState),
});

export default butikkReducer;

function finnSisteButikknavn(handlinger) {
    const sistebutikk = [...handlinger].pop();
    return sistebutikk.butikk;
}
