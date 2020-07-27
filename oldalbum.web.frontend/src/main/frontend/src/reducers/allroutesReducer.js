import { createReducer } from '@reduxjs/toolkit';
import {
    ALLROUTES_RECEIVE,
} from '../reduxactions';

const allroutesReducer = createReducer([], {
    [ALLROUTES_RECEIVE]: (state, action) => action.payload,
});

export default allroutesReducer;
