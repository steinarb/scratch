import { createReducer } from '@reduxjs/toolkit';
import {
    SELECT_PICTURE_ALBUMENTRY,
    UNSELECT_PICTURE_ALBUMENTRY,
    CLEAR_SELECTION,
} from '../reduxactions';

const selectedentriesReducer = createReducer([], {
    [SELECT_PICTURE_ALBUMENTRY]: (state, action) => addIfNotPresent(state, action.payload),
    [UNSELECT_PICTURE_ALBUMENTRY]: (state, action) => removeIfPresent(state, action.payload),
    [CLEAR_SELECTION]: () => [],
});

export default selectedentriesReducer;

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
