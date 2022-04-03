import { createReducer } from '@reduxjs/toolkit';
import {
    DATO_ENDRE,
    NYHANDLING_LAGRET,
} from '../actiontypes';

const defaultVerdi = new Date().toISOString();

const handletidspunktReducer = createReducer(defaultVerdi, {
    [DATO_ENDRE]: (state, action) =>  new Date(action.payload).toISOString(),
    [NYHANDLING_LAGRET]: () => new Date().toISOString(),
});

export default handletidspunktReducer;
