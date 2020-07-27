import { createReducer } from '@reduxjs/toolkit';
import { ALLROUTES_RECEIVE } from '../reduxactions';
import { addWebcontextToPath } from '../common';

// Creates a map from id to array of children
const childentriesReducer = createReducer(new Map(), {
    [ALLROUTES_RECEIVE]: (state, action) => action.payload.map(addWebcontextToPath).reduce(addChildToParent, new Map()),
});

export default childentriesReducer;

function addChildToParent(accumulator, item) {
    const { parent } = item;
    if (parent) {
        if (!accumulator.has(parent)) { accumulator.set(parent, []); }
        accumulator.get(parent).push(item);
    }

    return accumulator;
}
