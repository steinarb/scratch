import { createSlice, createAction } from '@reduxjs/toolkit';
import moment from 'moment';
import handlinger from './handlinger';
const { HANDLINGER_MOTTA } = handlinger.actions;

const defaultState = {
    handletidspunkt: moment(),
    belop: 0.0,
    storeId: -1,
};

const nyhandling = createSlice({
    name: 'nyhandling',
    initialState: defaultState,
    reducers: {
        BELOP_ENDRE: (state, action) => {
            const belop = (!action.payload || isNaN(action.payload)) ? 0 : ((action.payload.endsWith('.') ? action.payload : parseFloat(action.payload)));
            return { ...state, belop };
        },
        BUTIKK_ENDRE: (state, action) => {
            const storeId = action.payload;
            return { ...state, storeId };
        },
        DATO_ENDRE: (state, action) => {
            const handletidspunkt = action.payload;
            return { ...state, handletidspunkt };
        },
        NYHANDLING_LAGRET: (state, action) => ({ ...state, belop: 0, handletidspunkt: moment() }),
    },
    extraReducers: {
        HANDLINGER_MOTTA: (state, action) => {
            const sistebutikk = [...action.payload].pop();
            const storeId = sistebutikk.storeId;
            return { ...state, storeId };
        },
    },
});

export const NYHANDLING_REGISTRER = createAction('NYHANDLING_REGISTRER');

export default nyhandling;
