import { createSlice, createAction } from '@reduxjs/toolkit';

const sumyearmonth = createSlice({
    name: 'sumyearmonth',
    initialState: [],
    reducers: {
        SUMYEARMONTH_MOTTA: (state, action) => action.payload,
    },
});

export const SUMYEARMONTH_HENT = createAction('SUMYEARMONTH_HENT');

export default sumyearmonth;
