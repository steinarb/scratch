import { createReducer } from '@reduxjs/toolkit';
import {
    VELG_BUTIKK,
} from '../actiontypes';

const valgtButikkReducer = createReducer(-1, {
    [VELG_BUTIKK]: (state, action) => {
        const { indeks } = action.payload;
        return indeks;
    },
});

export default valgtButikkReducer;
