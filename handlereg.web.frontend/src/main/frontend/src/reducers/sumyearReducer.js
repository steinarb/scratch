import { createSlice, createAction } from '@reduxjs/toolkit';

const sumyear = createSlice({
    name: 'sumyear',
    initialState: [],
    reducers: {
        SUMYEAR_MOTTA: (state, action) => action.payload,
    },
});

export const SUMYEAR_HENT = createAction('SUMYEAR_HENT');

export default sumyear;
