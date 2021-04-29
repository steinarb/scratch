import { createSlice, createAction } from '@reduxjs/toolkit';

const sistehandel = createSlice({
    name: 'sistehandel',
    initialState: [],
    reducers: {
        SISTEHANDEL_MOTTA: (state, action) => action.payload,
    },
});

export const SISTEHANDEL_HENT = createAction('SISTEHANDEL_HENT');

export default sistehandel;
