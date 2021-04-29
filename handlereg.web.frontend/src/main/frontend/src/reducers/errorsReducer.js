import { createSlice } from '@reduxjs/toolkit';

const errors = createSlice({
    name: 'errors',
    initialState: {},
    reducers: {
        LOGIN_ERROR: (state, action) => {
            const login = action.payload;
            return { ...state, login };
        },
        OVERSIKT_ERROR: (state, action) => {
            const oversikt = action.payload;
            return { ...state, oversikt };
        },
        HANDLINGER_ERROR: (state, action) => {
            const handlinger = action.payload;
            return { ...state, handlinger };
        },
        NYHANDLING_ERROR: (state, action) => {
            const nyhandling = action.payload;
            return { ...state, nyhandling };
        },
        BUTIKKER_ERROR: (state, action) => {
            const butikker = action.payload;
            return { ...state, butikker };
        },
        NYBUTIKK_ERROR: (state, action) => {
            const nybutikk = action.payload;
            return { ...state, nybutikk };
        },
        SUMBUTIKK_ERROR: (state, action) => {
            const sumbutikk = action.payload;
            return { ...state, sumbutikk };
        },
        HANDLINGERBUTIKK_ERROR: (state, action) => {
            const handlingerbutikk = action.payload;
            return { ...state, handlingerbutikk };
        },
        SISTEHANDEL_ERROR: (state, action) => {
            const sistehandel = action.payload;
            return { ...state, sistehandel };
        },
        SUMYEAR_ERROR: (state, action) => {
            const sumyear = action.payload;
            return { ...state, sumyear };
        },
        SUMYEARMONTH_ERROR: (state, action) => {
            const sumyearmonth = action.payload;
            return { ...state, sumyearmonth };
        },
    },
});

export default errors;
