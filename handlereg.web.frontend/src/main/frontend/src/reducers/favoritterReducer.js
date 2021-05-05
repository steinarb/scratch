import { createReducer } from '@reduxjs/toolkit';
import {
    FAVORITTER_MOTTA,
} from '../actiontypes';

const favoritterReducer = createReducer([], {
    [FAVORITTER_MOTTA]: (state, action) => action.payload,
});

export default favoritterReducer;
