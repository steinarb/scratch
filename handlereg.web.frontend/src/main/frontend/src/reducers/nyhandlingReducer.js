import { createReducer } from '@reduxjs/toolkit';
import {
    BELOP_ENDRE,
    BUTIKK_ENDRE,
    DATO_ENDRE,
    NYHANDLING_LAGRET,
    HANDLINGER_MOTTA,
} from '../actiontypes';

const defaultState = {
    handletidspunkt: new Date().toISOString(),
    belop: 0.0,
    storeId: -1,
};

const nyhandlingReducer = createReducer(defaultState, {
    [BELOP_ENDRE]: (state, action) => {
        const belop = (!action.payload || isNaN(action.payload)) ? 0 : ((action.payload.endsWith('.') ? action.payload : parseFloat(action.payload)));
        return { ...state, belop };
    },
    [BUTIKK_ENDRE]: (state, action) => {
        const storeId = action.payload;
        return { ...state, storeId };
    },
    [DATO_ENDRE]: (state, action) => {
        const handletidspunkt = new Date(action.payload).toISOString();
        return { ...state, handletidspunkt };
    },
    [NYHANDLING_LAGRET]: (state) => ({ ...state, belop: 0, handletidspunkt: (new Date()).toISOString() }),
    [HANDLINGER_MOTTA]: (state, action) => {
        const sistebutikk = [...action.payload].pop();
        const storeId = sistebutikk.storeId;
        return { ...state, storeId };
    },
});

export default nyhandlingReducer;
