import { createReducer } from '@reduxjs/toolkit';
import {
    BUTIKK_ENDRE,
    HANDLINGER_MOTTA,
} from '../actiontypes';

const storeIdReducer = createReducer(-1, {
    [BUTIKK_ENDRE]: (state, action) => action.payload,
    [HANDLINGER_MOTTA]: (state, action) => finnSisteButikk(action.payload),
});

export default storeIdReducer;

function finnSisteButikk(handlinger) {
    const sistebutikk = [...handlinger].pop();
    return sistebutikk.storeId;
}
