import { createReducer } from '@reduxjs/toolkit';
import {
    BELOP_ENDRE,
    BUTIKK_ENDRE,
    DATO_ENDRE,
    NYHANDLING_LAGRET,
    HANDLINGER_MOTTA,
} from '../actiontypes';
import moment from 'moment';

const defaultState = {
    handletidspunkt: moment(),
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
        const handletidspunkt = moment(action.payload);
        return { ...state, handletidspunkt };
    },
    [NYHANDLING_LAGRET]: (state) => ({ ...state, belop: 0, handletidspunkt: moment() }),
    [HANDLINGER_MOTTA]: (state, action) => {
        const sistebutikk = [...action.payload].pop();
        const storeId = sistebutikk.storeId;
        return { ...state, storeId };
    },
});

export default nyhandlingReducer;
