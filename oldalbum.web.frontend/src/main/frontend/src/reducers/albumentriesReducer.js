import { createReducer } from '@reduxjs/toolkit';
import { ALLROUTES_RECEIVE } from '../reduxactions';
import { addWebcontextToPath } from '../common';

const albumentriesReducer = createReducer({}, {
    [ALLROUTES_RECEIVE]: (state, action) => {
        const entries = {};
        action.payload.map(addWebcontextToPath).forEach(e => entries[e.id] = { ...e });
        return entries;
    },
});

export default albumentriesReducer;
