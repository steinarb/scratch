import { createReducer } from '@reduxjs/toolkit';
import {
    SELECT_PICTURE_ALBUMENTRY,
    CLEAR_SELECTION,
} from '../reduxactions';

const selectedentriesReducer = createReducer([], (builder) => {
    builder
        .addCase(SELECT_PICTURE_ALBUMENTRY, (state, action) => selectOrUnselectAlbumEntry(state, action.payload))
        .addCase(CLEAR_SELECTION, () => []);
});

export default selectedentriesReducer;

function selectOrUnselectAlbumEntry(state, payload) {
    const { entry, selected } = payload;
    if (selected) {
        return addIfNotPresent(state, entry);
    } else {
        return removeIfPresent(state, entry);
    }
}

function addIfNotPresent(state, entry) {
    if (state.findIndex(e => e.id === entry.id) < 0) {
        const nextState = [ ...state ];
        nextState.push(entry);
        return nextState;
    }

    return state;
}

function removeIfPresent(state, entry) {
    const index = state.findIndex(e => e.id === entry.id);
    if (index > -1) {
        return state.toSpliced(index, 1);
    }

    return state;
}
