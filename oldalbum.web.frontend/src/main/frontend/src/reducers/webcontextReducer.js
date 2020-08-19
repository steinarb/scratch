import { createReducer } from '@reduxjs/toolkit';
import { WEBCONTEXT_SET } from '../reduxactions';

// Creates a map from id to array of children
const webcontextReducer = createReducer('', {
    [WEBCONTEXT_SET]: (state, action) => action.payload,
});

export default webcontextReducer;
