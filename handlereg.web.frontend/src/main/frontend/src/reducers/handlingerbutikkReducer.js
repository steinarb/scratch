import { createSlice, createAction } from '@reduxjs/toolkit';

const handlingerbutikk = createSlice({
    name: 'handlingerbutikk',
    initialState: [],
    reducers: {
        HANDLINGERBUTIKK_MOTTA: (state, action) => action.payload,
    },
});

export const HANDLINGERBUTIKK_HENT = createAction('HANDLINGERBUTIKK_HENT');

export default handlingerbutikk;
