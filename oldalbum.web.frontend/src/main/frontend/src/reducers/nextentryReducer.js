import { createReducer } from '@reduxjs/toolkit';
import { ALLROUTES_RECEIVE } from '../reduxactions';

// Creates a map from id to array of children
const nextentryReducer = createReducer({}, {
    [ALLROUTES_RECEIVE]: (state, action) => {
        const next = {};
        action.payload.forEach(e => next[e.id] = findNext(e, action.payload));
        return next;
    },
});

export default nextentryReducer;

function findNext(item, allroutes) {
    if (!item.parent) { return undefined; }
    const parent = allroutes.find(r => r.id === item.parent) || {};
    if (item.sort >= parent.childcount) { return undefined; }
    const siblings = allroutes.filter(r => r.parent === item.parent).sort((a,b) => a.sort - b.sort);
    return siblings[siblings.findIndex(s => s.id === item.id) + 1];
}
