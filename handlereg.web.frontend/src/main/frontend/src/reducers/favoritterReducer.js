import { createReducer } from '@reduxjs/toolkit';
import {
    FAVORITTER_MOTTA,
} from '../actiontypes';
import { emptyAccount } from '../constants';

const favoritterReducer = createReducer([], {
    [FAVORITTER_MOTTA]: (state, action) => action.payload,
});

export default favoritterReducer;
