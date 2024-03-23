import { createReducer } from '@reduxjs/toolkit';
import {
    SHARE_LINK,
    CLEAR_SHARED_LINK_ITEM,
} from '../reduxactions';
const initialState = {};

const sharedLinkItemReducer = createReducer(initialState, (builder) => {
    builder
        .addCase(SHARE_LINK, (state, action) => action.payload)
        .addCase(CLEAR_SHARED_LINK_ITEM, () => initialState);
});

export default sharedLinkItemReducer;
