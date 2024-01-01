import { createReducer } from '@reduxjs/toolkit';
import {
    BATCH_ADD_URL_FIELD_CHANGED,
    CLEAR_BATCH_ADD_URL_FIELD,
} from '../reduxactions';
const initialState = '';

const batchAddUrlReducer = createReducer(initialState, builder => {
    builder
        .addCase(BATCH_ADD_URL_FIELD_CHANGED, (state, action) => action.payload)
        .addCase(CLEAR_BATCH_ADD_URL_FIELD, () => initialState);
});

export default batchAddUrlReducer;
