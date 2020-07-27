import { createReducer } from '@reduxjs/toolkit';
import { ALLROUTES_RECEIVE } from '../reduxactions';
import { addWebcontextToPath } from '../common';

const allroutesReducer = createReducer([], {
    [ALLROUTES_RECEIVE]: (state, action) => action.payload.map(addWebcontextToPath),
});

export default allroutesReducer;
