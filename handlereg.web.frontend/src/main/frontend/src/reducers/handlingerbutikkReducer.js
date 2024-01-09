import { createReducer } from '@reduxjs/toolkit';
import {
    HANDLINGERBUTIKK_MOTTA,
} from '../actiontypes';

const handlingerbutikkReducer = createReducer([], builder => {
    builder
        .addCase(HANDLINGERBUTIKK_MOTTA, (state, action) => action.payload);
});

export default handlingerbutikkReducer;
