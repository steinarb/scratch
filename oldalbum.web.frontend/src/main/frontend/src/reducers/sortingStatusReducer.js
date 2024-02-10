import { createReducer } from '@reduxjs/toolkit';
import {
    SORT_ALBUM_ENTRIES_BY_DATE_REQUEST,
    SORT_ALBUM_ENTRIES_BY_DATE_RECEIVE,
    SORT_ALBUM_ENTRIES_BY_DATE_FAILURE,
} from '../reduxactions';

const sortingStatusReducer = createReducer('', (builder) => {
    builder
        .addCase(SORT_ALBUM_ENTRIES_BY_DATE_REQUEST, () => 'Sorting started')
        .addCase(SORT_ALBUM_ENTRIES_BY_DATE_RECEIVE, () => 'Sorting complete')
        .addCase(SORT_ALBUM_ENTRIES_BY_DATE_FAILURE, () => '');
});

export default sortingStatusReducer;
