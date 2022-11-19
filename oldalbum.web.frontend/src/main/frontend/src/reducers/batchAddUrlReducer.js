import { createReducer } from '@reduxjs/toolkit';
import {
    BATCH_ADD_URL_FIELD_CHANGED,
    CLEAR_BATCH_ADD_URL_FIELD,
} from '../reduxactions';
const initialState = '';

const batchAddUrlReducer = createReducer(initialState, {
    [BATCH_ADD_URL_FIELD_CHANGED]: (state, action) => action.payload,
    [CLEAR_BATCH_ADD_URL_FIELD]: () => initialState,
});

export default batchAddUrlReducer;
