import { createReducer } from '@reduxjs/toolkit';
import {
    DEFAULT_TITLE_FIELD_CHANGED,
    CLEAR_BATCH_ADD_URL_FIELD,
} from '../reduxactions';
const initialState = '';

const batchAddDefaultTitle = createReducer(initialState, builder => {
    builder
        .addCase(DEFAULT_TITLE_FIELD_CHANGED, (state, action) => action.payload)
        .addCase(CLEAR_BATCH_ADD_URL_FIELD, () => initialState);
});

export default batchAddDefaultTitle;
