import { createReducer } from '@reduxjs/toolkit';
import {
    SHOW_EDIT_CONTROLS,
    HIDE_EDIT_CONTROLS,
} from '../reduxactions';

const showEditControlsReducer = createReducer(false, (builder) => {
    builder
        .addCase(SHOW_EDIT_CONTROLS, () => true)
        .addCase(HIDE_EDIT_CONTROLS, () => false);
});

export default showEditControlsReducer;
