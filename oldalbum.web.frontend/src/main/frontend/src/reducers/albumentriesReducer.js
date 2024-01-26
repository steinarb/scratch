import { createReducer } from '@reduxjs/toolkit';
import {
    ALLROUTES_RECEIVE,
    UPDATE_ALLROUTES,
} from '../reduxactions';

const albumentriesReducer = createReducer({}, builder => {
    builder
        .addCase(ALLROUTES_RECEIVE, (state, action) => createMapFromIdToAlbumentry(action.payload))
        .addCase(UPDATE_ALLROUTES, (state, action) => createMapFromIdToAlbumentry(action.payload));
});

export default albumentriesReducer;

function createMapFromIdToAlbumentry(allroutes) {
    const entries = {};
    allroutes.forEach(e => entries[e.id] = { ...e });
    return entries;
}
