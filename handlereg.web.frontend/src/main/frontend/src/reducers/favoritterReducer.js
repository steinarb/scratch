import { createReducer } from '@reduxjs/toolkit';
import {
    FAVORITTER_MOTTA,
} from '../actiontypes';

const favoritterReducer = createReducer([], builder => {
    builder
        .addCase(FAVORITTER_MOTTA, (state, action) => action.payload);
});

export default favoritterReducer;
