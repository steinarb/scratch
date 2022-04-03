import { createReducer } from '@reduxjs/toolkit';
import {
    NYBUTIKK_LAGRET,
    BUTIKK_LAGRET,
    BUTIKKER_MOTTA,
} from '../actiontypes';

const defaultState = [];


const butikkerReducer = createReducer(defaultState, {
    [BUTIKKER_MOTTA]: (state, action) => action.payload,
    [NYBUTIKK_LAGRET]: (state, action) => action.payload,
    [BUTIKK_LAGRET]: (state, action) => action.payload,
});


export default butikkerReducer;
