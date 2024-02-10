import { createReducer } from '@reduxjs/toolkit';
import {
    SET_MODIFY_FAILED_ERROR,
    CLEAR_ALERT,
    SORT_ALBUM_ENTRIES_BY_DATE_REQUEST,
} from '../reduxactions';

const modifyFailedErrorReducer = createReducer('', (builder) => {
    builder
        .addCase(SET_MODIFY_FAILED_ERROR, (state, action) => action.payload)
        .addCase(SORT_ALBUM_ENTRIES_BY_DATE_REQUEST, () => '')
        .addCase(CLEAR_ALERT, () => '');
});

export default modifyFailedErrorReducer;
