import { createReducer } from '@reduxjs/toolkit';
import { ALLROUTES_RECEIVE } from '../reduxactions';
import { addWebcontextToPath } from '../common';

// Creates a map from id to array of children
const childentriesReducer = createReducer({}, {
    [ALLROUTES_RECEIVE]: (state, action) => {
        const children = {};
        action.payload.map(addWebcontextToPath).forEach(e => addChildToParent(children, e));
        return children;
    },
});

export default childentriesReducer;

function addChildToParent(state, item) {
    const { parent } = item;
    if (parent) {
        if (parent in state) {
            state[parent].push({ ...item });
        } else {
            state[parent] = [{ ...item }];
        }
    }
}
