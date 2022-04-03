import { createReducer } from '@reduxjs/toolkit';
import {
    BELOP_ENDRE,
    NYHANDLING_LAGRET,
} from '../actiontypes';

const belopReducer = createReducer(0, {
    [BELOP_ENDRE]: (state, action) => (!action.payload || isNaN(action.payload)) ? 0 : ((action.payload.endsWith('.') ? action.payload : parseFloat(action.payload))),
    [NYHANDLING_LAGRET]: () => 0,
});

export default belopReducer;
