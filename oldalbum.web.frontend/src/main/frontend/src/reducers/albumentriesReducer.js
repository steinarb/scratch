import { createReducer } from '@reduxjs/toolkit';
import { ALLROUTES_RECEIVE } from '../reduxactions';
import { addWebcontextToPath } from '../common';

const albumentriesReducer = createReducer({}, {
    [ALLROUTES_RECEIVE]: (state, action) => action.payload.reduce((acc, e) => acc[e.id.toString()] = addWebcontextToPath(e), {}),
});

export default albumentriesReducer;
