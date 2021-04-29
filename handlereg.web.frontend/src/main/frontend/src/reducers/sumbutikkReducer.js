import { createSlice, createAction } from '@reduxjs/toolkit';

const sumbutikk = createSlice({
    name: 'sumbutikk',
    initialState: [],
    reducers: {
        SUMBUTIKK_MOTTA: (state, action) => action.payload,
    },
});

export const SUMBUTIKK_HENT = createAction('SUMBUTIKK_HENT');

export default sumbutikk;
