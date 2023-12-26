import { createReducer } from '@reduxjs/toolkit';
import {
    SET_ALBUM_SHOW_YEARS,
    SET_ALBUM_HIDE_YEARS,
} from '../reduxactions';

const albumShowYearsReducer = createReducer({}, {
    [SET_ALBUM_SHOW_YEARS]: (state, action) => setAlbumShowYears(state, action.payload),
    [SET_ALBUM_HIDE_YEARS]: (state, action) => setAlbumHideYears(state, action.payload),
});

function setAlbumShowYears(state, album) {
    const nextState = { ...state };
    nextState[album] = true;
    return nextState;
}

function setAlbumHideYears(state, album) {
    const nextState = { ...state };
    nextState[album] = false;
    return nextState;
}

export default albumShowYearsReducer;
