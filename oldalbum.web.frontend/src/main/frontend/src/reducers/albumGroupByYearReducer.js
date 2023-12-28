import { createReducer } from '@reduxjs/toolkit';
import Cookies from 'js-cookie';
import {
    ALLROUTES_RECEIVE,
    SET_ALBUM_GROUP_BY_YEAR,
    UNSET_ALBUM_GROUP_BY_YEAR,
} from '../reduxactions';

const initialGroupByAlbumSetting = JSON.parse(Cookies.get('albumGroupByYear') || '{}');

const groupByYearReducer = createReducer(initialGroupByAlbumSetting, {
    [ALLROUTES_RECEIVE]: (state, action) => saveGroupByYearInitialState(state, action.payload),
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

function saveGroupByYearInitialState(state, allroutes) {
    const groupByYearInitialState = {};
    for (const route of allroutes) {
        if (route.album && route.groupByYear !== undefined && route.groupByYear !== null) {
            groupByYearInitialState[route.id] = route.groupByYear;
        }
    }

    return Object.assign(groupByYearInitialState, state);
}

export default groupByYearReducer;
