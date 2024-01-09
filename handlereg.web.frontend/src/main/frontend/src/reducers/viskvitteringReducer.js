import { createReducer } from '@reduxjs/toolkit';
import {
    VIS_KVITTERING,
    NYHANDLING_LAGRET,
} from '../actiontypes';

const viskvitteringReducer = createReducer(false, builder => {
    builder
        .addCase(VIS_KVITTERING, (state, action) => action.payload)
        .addCase(NYHANDLING_LAGRET, () => true);
});

export default viskvitteringReducer;
