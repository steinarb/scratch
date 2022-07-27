import { createReducer } from '@reduxjs/toolkit';
import {
    HOME_VELG_BUTIKK,
    HANDLINGER_MOTTA,
} from '../actiontypes';

const storeIdReducer = createReducer(-1, {
    [HOME_VELG_BUTIKK]: (state, action) => action.payload,
    [HANDLINGER_MOTTA]: (state, action) => finnSisteButikk(action.payload),
});

export default storeIdReducer;

function finnSisteButikk(handlinger) {
    const sistebutikk = [...handlinger].pop();
    return sistebutikk.storeId;
}
