import { createReducer } from '@reduxjs/toolkit';
import {
    BUTIKKNAVN_ENDRE,
    NYBUTIKK_LAGRET,
    BUTIKK_LAGRET,
    VELG_BUTIKK,
} from '../actiontypes';

const defaultState = {
    butikknavn: '',
    gruppe: 2,
};

const butikkReducer = createReducer(defaultState, {
        [BUTIKKNAVN_ENDRE]: (state, action) => {
            const butikknavn = action.payload;
            return { ...state, butikknavn };
        },
        [NYBUTIKK_LAGRET]: () => ({ ...defaultState }),
        [BUTIKK_LAGRET]: () => ({ ...defaultState }),
        [VELG_BUTIKK]: (state, action) => {
            const { indeks, butikker } = action.payload;
            const valgtButikk = butikker[indeks];
            return { ...valgtButikk };
        },
});

export default butikkReducer;
