import { createReducer } from '@reduxjs/toolkit';
import {
    SELECT_PICTURE_ALBUMENTRY,
    UNSELECT_PICTURE_ALBUMENTRY,
} from '../reduxactions';

const selectedentriesReducer = createReducer([], {
    [SELECT_PICTURE_ALBUMENTRY]: (state, action) => addIfNotPresent(state, action.payload),
    [UNSELECT_PICTURE_ALBUMENTRY]: (state, action) => removeIfPresent(state, action.payload),
});

export default selectedentriesReducer;

function addIfNotPresent(state, entry) {
    if (!state.includes(entry)) {
        state.push(entry);
    }

    return state;
}

function removeIfPresent(state, entry) {
    const index = state.indexOf(entry);
    if (index > -1) {
        return state.toSpliced(index, 1);
    }

    return state;
}
