import { createSlice, createAction } from '@reduxjs/toolkit';

const defaultState = [];

const handlinger = createSlice({
    name: 'handlinger',
    initialState: defaultState,
    reducers: {
        HANDLINGER_MOTTA: (state, action) => action.payload,
    },
});

export const HANDLINGER_HENT = createAction('HANDLINGER_HENT');

export default handlinger;
