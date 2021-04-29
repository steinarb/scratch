import { createReducer } from '@reduxjs/toolkit';
import {
    OVERSIKT_MOTTA,
} from '../actiontypes';

const brukernavnReducer = createReducer('', {
    [OVERSIKT_MOTTA]: (state, action) => action.payload.brukernavn,
});

export default brukernavnReducer;
