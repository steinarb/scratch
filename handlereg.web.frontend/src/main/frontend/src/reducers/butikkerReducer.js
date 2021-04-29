import { createReducer } from '@reduxjs/toolkit';
import {
    NYBUTIKK_LAGRET,
    BUTIKK_LAGRET,
    BUTIKKER_MOTTA,
} from '../actiontypes';

const tomButikk = {
    storeId: -1,
    butikknavn: '',
    gruppe: 1,
    rekkefølge: 0,
};

const defaultState = [];

function leggPaaTomButikkIStarten(action) {
    const butikker = action.payload;
    butikker.unshift({ ...tomButikk });
    return butikker;
 }

const butikkerReducer = createReducer(defaultState, {
        [BUTIKKER_MOTTA]: (state, action) => leggPaaTomButikkIStarten(action),
        [NYBUTIKK_LAGRET]: (state, action) => leggPaaTomButikkIStarten(action),
        [BUTIKK_LAGRET]: (state, action) => leggPaaTomButikkIStarten(action),
});


export default butikkerReducer;
