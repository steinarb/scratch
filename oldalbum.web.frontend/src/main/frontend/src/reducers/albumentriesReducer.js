import { createReducer } from '@reduxjs/toolkit';
import { ALLROUTES_RECEIVE } from '../reduxactions';
import { addWebcontextToPath } from '../common';

const albumentriesReducer = createReducer([], {
    [ALLROUTES_RECEIVE]: (state, action) => changeRoutesIntoSparseArrayKeyedById(action.payload),
});

export default albumentriesReducer;

function changeRoutesIntoSparseArrayKeyedById(allroutes) {
    if (!allroutes) {
        return [];
    }

    const sparseArrayKeyedById = [];
    for (const albumentry of allroutes) {
        sparseArrayKeyedById[albumentry.id] = addWebcontextToPath(albumentry);
    }

    return sparseArrayKeyedById;
}
