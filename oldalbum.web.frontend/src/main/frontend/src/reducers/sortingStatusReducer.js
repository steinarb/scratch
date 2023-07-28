import { createReducer } from '@reduxjs/toolkit';
import {
    SORT_ALBUM_ENTRIES_BY_DATE_REQUEST,
    SORT_ALBUM_ENTRIES_BY_DATE_RECEIVE,
    SORT_ALBUM_ENTRIES_BY_DATE_FAILURE,
} from '../reduxactions';

const localeReducer = createReducer("NEVER_SORTED", {
    [SORT_ALBUM_ENTRIES_BY_DATE_REQUEST]: () => "SORTING_STARTED",
    [SORT_ALBUM_ENTRIES_BY_DATE_RECEIVE]: () => "SORTING_COMPLETE",
    [SORT_ALBUM_ENTRIES_BY_DATE_FAILURE]: () => "SORTING_FAILED",
});

export default localeReducer;
