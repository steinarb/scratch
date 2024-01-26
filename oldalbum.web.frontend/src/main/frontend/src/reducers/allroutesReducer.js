import { createReducer } from '@reduxjs/toolkit';
import {
    ALLROUTES_RECEIVE,
    UPDATE_ALLROUTES,
} from '../reduxactions';

const allroutesReducer = createReducer([], builder => {
    builder
        .addCase(ALLROUTES_RECEIVE, (state, action) => action.payload)
        .addCase(UPDATE_ALLROUTES, (state, action) => action.payload);
});

export default allroutesReducer;
