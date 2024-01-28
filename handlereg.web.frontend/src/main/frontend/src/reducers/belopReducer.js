import { createReducer } from '@reduxjs/toolkit';
import {
    BELOP_ENDRE,
    NYHANDLING_LAGRET,
} from '../actiontypes';

const belopReducer = createReducer(0, builder => {
    builder
        .addCase(BELOP_ENDRE, updateBelopOnKeyPress)
        .addCase(NYHANDLING_LAGRET, () => 0);
});

export default belopReducer;

function updateBelopOnKeyPress(state, action) {
    if (state === 0 && action.payload.endsWith('0')) {
        return parseFloat(action.payload[0]);
    }

    return (!action.payload || isNaN(action.payload)) ? 0 : parseFloat(action.payload);
}
