import { createReducer } from '@reduxjs/toolkit';
import { HOME_BUTIKKNAVN_ENDRE } from '../actiontypes';
import { api } from '../api';

const storeIdReducer = createReducer(-1, builder => {
    builder
        .addCase(HOME_BUTIKKNAVN_ENDRE, (state, action) => finnStoreIdSomMatcherButikknavn(state, action.payload))
        .addMatcher(api.endpoints.getHandlinger.matchFulfilled, (state, action) => finnSisteButikk(action.payload));
});

export default storeIdReducer;

function finnStoreIdSomMatcherButikknavn(state, payload) {
    const { navn, butikker = [] } = payload;
    return (butikker.find(b => b.butikknavn === navn) || {}).storeId || state;
}

function finnSisteButikk(handlinger) {
    const sistebutikk = [...handlinger].pop();
    return sistebutikk.storeId;
}
