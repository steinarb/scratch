import { createReducer } from '@reduxjs/toolkit';
import {
    FILL_MODIFY_ALBUM_FORM,
    FILL_ADD_ALBUM_FORM,
    FILL_MODIFY_PICTURE_FORM,
    FILL_ADD_PICTURE_FORM,
    MODIFY_ALBUM_GROUP_BY_YEAR_FIELD_CHANGED,
    ADD_ALBUM_GROUP_BY_YEAR_FIELD_CHANGED,
    MODIFY_PICTURE_GROUP_BY_YEAR_FIELD_CHANGED,
    ADD_PICTURE_GROUP_BY_YEAR_FIELD_CHANGED,
    CLEAR_ALBUM_FORM,
    CLEAR_PICTURE_FORM,
} from '../reduxactions';
const initialState = false;

const albumentryGroupByYearReducer = createReducer(initialState, {
    [FILL_MODIFY_ALBUM_FORM]: (state, action) => action.payload.groupByYear === undefined ? null : action.payload.groupByYear,
    [FILL_ADD_ALBUM_FORM]: (state, action) => action.payload.groupByYear === undefined ? null : action.payload.groupByYear,
    [FILL_MODIFY_PICTURE_FORM]: (state, action) => action.payload.groupByYear === undefined ? null : action.payload.groupByYear,
    [FILL_ADD_PICTURE_FORM]: (state, action) => action.payload.groupByYear === undefined ? null : action.payload.groupByYear,
    [MODIFY_ALBUM_GROUP_BY_YEAR_FIELD_CHANGED]: (state, action) => action.payload,
    [ADD_ALBUM_GROUP_BY_YEAR_FIELD_CHANGED]: (state, action) => action.payload,
    [MODIFY_PICTURE_GROUP_BY_YEAR_FIELD_CHANGED]: (state, action) => action.payload,
    [ADD_PICTURE_GROUP_BY_YEAR_FIELD_CHANGED]: (state, action) => action.payload,
    [CLEAR_ALBUM_FORM]: () => initialState,
    [CLEAR_PICTURE_FORM]: () => initialState,
});

export default albumentryGroupByYearReducer;
