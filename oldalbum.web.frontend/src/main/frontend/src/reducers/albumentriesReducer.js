import { createReducer } from '@reduxjs/toolkit';
import {
    ALLROUTES_RECEIVE,
} from '../reduxactions';

const albumentriesReducer = createReducer(new Map(), {
    [ALLROUTES_RECEIVE]: (state, action) => new Map(action.payload.map(e => [e.id, e])),
});

export default albumentriesReducer;
