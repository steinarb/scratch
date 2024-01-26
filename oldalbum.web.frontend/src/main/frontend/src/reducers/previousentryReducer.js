import { createReducer } from '@reduxjs/toolkit';
import {
    ALLROUTES_RECEIVE,
    UPDATE_ALLROUTES,
} from '../reduxactions';

// Creates a map from id to array of children
const previousentryReducer = createReducer({}, (builder) =>  {
    builder
        .addCase(ALLROUTES_RECEIVE, (state, action) => createMapFromIdToArrayOfChildren(action.payload))
        .addCase(UPDATE_ALLROUTES, (state, action) => createMapFromIdToArrayOfChildren(action.payload));
});

export default previousentryReducer;

function createMapFromIdToArrayOfChildren(allroutes) {
    const previous = {};
    allroutes.forEach(e => previous[e.id] = findPrevious(e, allroutes));
    return previous;
}

function findPrevious(item, allroutes) {
    if (!item.parent) { return undefined; }
    if (item.sort <= 1) { return undefined; }
    const siblings = allroutes.filter(r => r.parent === item.parent).sort((a,b) => a.sort - b.sort);
    return siblings[siblings.findIndex(s => s.id === item.id) - 1];
}
