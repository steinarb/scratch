import { createReducer } from '@reduxjs/toolkit';
import {
    LOGIN_ERROR,
    OVERSIKT_ERROR,
    HANDLINGER_ERROR,
    NYHANDLING_ERROR,
    BUTIKKER_ERROR,
    NYBUTIKK_ERROR,
    SUMBUTIKK_ERROR,
    HANDLINGERBUTIKK_ERROR,
    SISTEHANDEL_ERROR,
    SUMYEAR_ERROR,
    SUMYEARMONTH_ERROR,
} from '../actiontypes';

const errorsReducer = createReducer({}, builder => {
    builder
        .addCase(LOGIN_ERROR, (state, action) => {
            const login = action.payload;
            return { ...state, login };
        })
        .addCase(OVERSIKT_ERROR, (state, action) => {
            const oversikt = action.payload;
            return { ...state, oversikt };
        })
        .addCase(HANDLINGER_ERROR, (state, action) => {
            const handlinger = action.payload;
            return { ...state, handlinger };
        })
        .addCase(NYHANDLING_ERROR, (state, action) => {
            const nyhandling = action.payload;
            return { ...state, nyhandling };
        })
        .addCase(BUTIKKER_ERROR, (state, action) => {
            const butikker = action.payload;
            return { ...state, butikker };
        })
        .addCase(NYBUTIKK_ERROR, (state, action) => {
            const nybutikk = action.payload;
            return { ...state, nybutikk };
        })
        .addCase(SUMBUTIKK_ERROR, (state, action) => {
            const sumbutikk = action.payload;
            return { ...state, sumbutikk };
        })
        .addCase(HANDLINGERBUTIKK_ERROR, (state, action) => {
            const handlingerbutikk = action.payload;
            return { ...state, handlingerbutikk };
        })
        .addCase(SISTEHANDEL_ERROR, (state, action) => {
            const sistehandel = action.payload;
            return { ...state, sistehandel };
        })
        .addCase(SUMYEAR_ERROR, (state, action) => {
            const sumyear = action.payload;
            return { ...state, sumyear };
        })
        .addCase(SUMYEARMONTH_ERROR, (state, action) => {
            const sumyearmonth = action.payload;
            return { ...state, sumyearmonth };
        });
});

export default errorsReducer;
