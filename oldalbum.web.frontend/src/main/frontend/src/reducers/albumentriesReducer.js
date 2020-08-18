import { createReducer } from '@reduxjs/toolkit';
import { ALLROUTES_RECEIVE } from '../reduxactions';

const albumentriesReducer = createReducer({}, {
    [ALLROUTES_RECEIVE]: (state, action) => {
        const entries = {};
        action.payload.forEach(e => entries[e.id] = { ...e });
        return entries;
    },
});

export default albumentriesReducer;
