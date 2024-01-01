import { createReducer } from '@reduxjs/toolkit';
import { SET_CHILDENTRIES_BY_YEAR } from '../reduxactions';

// Creates a map from id to array of children
const childentriesByYearReducer = createReducer({}, builder => {
    builder
        .addCase(SET_CHILDENTRIES_BY_YEAR, (state, action) => action.payload);
});

export default childentriesByYearReducer;
