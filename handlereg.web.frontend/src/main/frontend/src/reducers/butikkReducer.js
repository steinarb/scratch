import { createSlice, createAction } from '@reduxjs/toolkit';
import moment from 'moment';

const defaultState = {
    butikknavn: '',
    gruppe: 2,
};

const butikk = createSlice({
    name: 'butikk',
    initialState: defaultState,
    reducers: {
        BUTIKKNAVN_ENDRE: (state, action) => {
            const butikknavn = action.payload;
            return { ...state, butikknavn };
        },
        NYBUTIKK_LAGRET: (state, action) => ({ ...defaultState }),
        BUTIKK_LAGRET: (state, action) => ({ ...defaultState }),
        VELG_BUTIKK: (state, action) => {
            const { indeks, butikker } = action.payload;
            const valgtButikk = butikker[indeks];
            return { ...valgtButikk };
        },
    }
});

export const NYBUTIKK_REGISTRER = createAction('NYBUTIKK_REGISTRER');
export const BUTIKK_LAGRE = createAction('BUTIKK_LAGRE');

export default butikk;
