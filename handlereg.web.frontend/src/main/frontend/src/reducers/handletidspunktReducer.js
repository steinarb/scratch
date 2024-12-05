import { createReducer } from '@reduxjs/toolkit';
import { DATO_ENDRE } from '../actiontypes';
import { api } from '../api';

const defaultVerdi = new Date().toISOString();

const handletidspunktReducer = createReducer(defaultVerdi, builder => {
    builder
        .addCase(DATO_ENDRE, (state, action) =>  action.payload + 'T' + state.split('T')[1])
        .addMatcher(api.endpoints.postNyhandling.matchFulfilled, () => new Date().toISOString());
});

export default handletidspunktReducer;
