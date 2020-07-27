import { createReducer } from '@reduxjs/toolkit';
import { ALLROUTES_RECEIVE } from '../reduxactions';
import { addWebcontextToPath } from '../common';

const albumentriesReducer = createReducer(new Map(), {
    [ALLROUTES_RECEIVE]: (state, action) => new Map(action.payload.map(e => [e.id, addWebcontextToPath(e)])),
});

export default albumentriesReducer;
