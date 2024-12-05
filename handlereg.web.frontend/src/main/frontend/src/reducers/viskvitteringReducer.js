import { createReducer } from '@reduxjs/toolkit';
import { VIS_KVITTERING } from '../actiontypes';
import { api } from '../api';

const viskvitteringReducer = createReducer(false, builder => {
    builder
        .addCase(VIS_KVITTERING, (state, action) => action.payload)
        .addMatcher(api.endpoints.postNyhandling.matchFulfilled, () => true);
});

export default viskvitteringReducer;
