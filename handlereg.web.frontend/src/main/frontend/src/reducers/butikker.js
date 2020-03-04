import { createSlice, createAction } from '@reduxjs/toolkit';
import butikk from './butikk';

const { NYBUTIKK_LAGRET, BUTIKK_LAGRET } = butikk.actions;

const tomButikk = {
    storeId: -1,
    butikknavn: '',
    gruppe: 1,
    rekkefølge: 0,
};

const defaultState = [];

function leggPaaTomButikkIStarten(action) {
    const butikker = action.payload;
    butikker.unshift(tomButikk);
    return butikker;
 }

const butikker = createSlice({
    name: 'butikker',
    initialState: [],
    reducers: {
        BUTIKKER_MOTTA: (state, action) => leggPaaTomButikkIStarten(action),
    },
    extraReducers: {
        [NYBUTIKK_LAGRET]: (state, action) => leggPaaTomButikkIStarten(action),
        [BUTIKK_LAGRET]: (state, action) => leggPaaTomButikkIStarten(action),
    },
});

export const BUTIKKER_HENT = createAction('BUTIKKER_HENT');

export default butikker;
