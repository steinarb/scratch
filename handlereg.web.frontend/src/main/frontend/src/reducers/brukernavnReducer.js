import { createReducer } from '@reduxjs/toolkit';
import oversikt from '../reducers/oversikt';
const { OVERSIKT_MOTTA } = oversikt.actions;

const brukernavnReducer = createReducer('', {
    [OVERSIKT_MOTTA]: (state, action) => action.payload.brukernavn,
});

export default brukernavnReducer;
