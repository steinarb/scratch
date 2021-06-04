import { createReducer } from '@reduxjs/toolkit';
import {
    VIS_KVITTERING,
    NYHANDLING_LAGRET,
} from '../actiontypes';

const viskvitteringReducer = createReducer(false, {
    [VIS_KVITTERING]: (state, action) => action.payload,
    [NYHANDLING_LAGRET]: () => true,
});

export default viskvitteringReducer;
