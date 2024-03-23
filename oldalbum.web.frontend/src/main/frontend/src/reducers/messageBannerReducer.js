import { createReducer } from '@reduxjs/toolkit';
import {
    SET_MESSAGE_BANNER,
    CLEAR_MESSAGE_BANNER,
} from '../reduxactions';

const messageBannerReducer = createReducer('', (builder) => {
    builder
        .addCase(SET_MESSAGE_BANNER, (state, action) => action.payload)
        .addCase(CLEAR_MESSAGE_BANNER, () => '');
});

export default messageBannerReducer;
