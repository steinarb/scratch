import { createSlice, createAction } from '@reduxjs/toolkit';
import butikk from './butikk';

const { VELG_BUTIKK } = butikk.actions;

const defaultState = -1;

const valgtButikk = createSlice({
    name: 'valgtButikk',
    initialState: defaultState,
    extraReducers: {
        [VELG_BUTIKK]: (state, action) => {
            const { indeks } = action.payload;
            return indeks;
        },
    },
});

export default valgtButikk;
