import { createReducer } from '@reduxjs/toolkit';
import {
    ALLROUTES_ERROR,
} from '../reduxactions';

const paymenttypeReducer = createReducer({}, {
    [ALLROUTES_ERROR]: (state, action) => {
        const allroutes = action.payload;
        return { ...state, allroutes };
    },
});

export default paymenttypeReducer;
