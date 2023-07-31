import { createReducer } from '@reduxjs/toolkit';
import {
    SORT_ALBUM_ENTRIES_BY_DATE_REQUEST,
    SORT_ALBUM_ENTRIES_BY_DATE_RECEIVE,
    SORT_ALBUM_ENTRIES_BY_DATE_FAILURE,
} from '../reduxactions';

const localeReducer = createReducer('', {
    [SORT_ALBUM_ENTRIES_BY_DATE_REQUEST]: () => 'Sorting started',
    [SORT_ALBUM_ENTRIES_BY_DATE_RECEIVE]: () => 'Sorting complete',
    [SORT_ALBUM_ENTRIES_BY_DATE_FAILURE]: () => 'Sorting failed',
});

export default localeReducer;
