import { createSlice, createAction } from '@reduxjs/toolkit';

const defaultState = {
    oversiktresultat: {},
};

const oversikt = createSlice({
    name: 'oversikt',
    initialState: defaultState,
    reducers: {
        OVERSIKT_MOTTA: (state, action) => action.payload,
        NYHANDLING_LAGRET: (state, action) => action.payload,
    },
});

export const OVERSIKT_HENT = createAction('OVERSIKT_HENT');

export default oversikt;
