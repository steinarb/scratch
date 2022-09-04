import { createReducer } from '@reduxjs/toolkit';
import { SET_BASENAME } from '../reduxactions';

const basenameReducer = createReducer('', {
    [SET_BASENAME]: (state, action) => action.payload,
});

export default basenameReducer;
