import { createReducer } from '@reduxjs/toolkit';
import { ALLROUTES_RECEIVE } from '../reduxactions';

// Creates a map from id to array of children
const previousentryReducer = createReducer({}, {
    [ALLROUTES_RECEIVE]: (state, action) => {
        const previous = {};
        action.payload.forEach(e => previous[e.id] = findPrevious(e, action.payload));
        return previous;
    },
});

export default previousentryReducer;

function findPrevious(item, allroutes) {
    if (!item.parent) { return undefined; }
    if (item.sort <= 1) { return undefined; }
    const siblings = allroutes.filter(r => r.parent === item.parent).sort((a,b) => a.sort - b.sort);
    return siblings[siblings.findIndex(s => s.id === item.id) - 1];
}
