import { createReducer } from '@reduxjs/toolkit';
import Cookies from 'js-cookie';
import {
    SET_ALBUM_GROUP_BY_YEAR,
    UNSET_ALBUM_GROUP_BY_YEAR,
} from '../reduxactions';

const initialGroupByAlbumSetting = JSON.parse(Cookies.get('albumGroupByYear') || '{}');

const groupByYearReducer = createReducer(initialGroupByAlbumSetting, {
    [SET_ALBUM_GROUP_BY_YEAR]: (state, action) => setAlbumGroupByYear(state, action.payload),
    [UNSET_ALBUM_GROUP_BY_YEAR]: (state, action) => unsetAlbumGroupByYear(state, action.payload),
});

function setAlbumGroupByYear(state, album) {
    const nextState = { ...state };
    nextState[album] = true;
    return nextState;
}

function unsetAlbumGroupByYear(state, album) {
    const nextState = { ...state };
    nextState[album] = false;
    return nextState;
}

export default groupByYearReducer;
