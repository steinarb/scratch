import { createReducer } from '@reduxjs/toolkit';
import {
    SISTEHANDEL_MOTTA,
} from '../actiontypes';

const sistehandelReducer = createReducer([], {
    [SISTEHANDEL_MOTTA]: (state, action) => action.payload,
});

export default sistehandelReducer;
