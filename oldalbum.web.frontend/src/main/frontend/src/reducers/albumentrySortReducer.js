import { createReducer } from '@reduxjs/toolkit';
import {
    FILL_MODIFY_ALBUM_FORM,
    FILL_ADD_ALBUM_FORM,
    FILL_MODIFY_PICTURE_FORM,
    FILL_ADD_PICTURE_FORM,
    CLEAR_ALBUM_FORM,
    CLEAR_PICTURE_FORM,
} from '../reduxactions';
const initialState = 0;

const albumentryidReducer = createReducer(initialState, {
    [FILL_MODIFY_ALBUM_FORM]: (state, action) => action.payload.sort,
    [FILL_ADD_ALBUM_FORM]: (state, action) => action.payload.sort,
    [FILL_MODIFY_PICTURE_FORM]: (state, action) => action.payload.sort,
    [FILL_ADD_PICTURE_FORM]: (state, action) => action.payload.sort,
    [CLEAR_ALBUM_FORM]: () => initialState,
    [CLEAR_PICTURE_FORM]: () => initialState,
});

export default albumentryidReducer;
