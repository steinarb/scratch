import { createReducer } from '@reduxjs/toolkit';
import {
    SISTEHANDEL_MOTTA,
} from '../actiontypes';

const sistehandelReducer = createReducer([], builder => {
    builder
        .addCase(SISTEHANDEL_MOTTA, (state, action) => action.payload);
});

export default sistehandelReducer;
