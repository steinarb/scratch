import { createReducer } from '@reduxjs/toolkit';
import {
    MODIFY_PASSWORD2,
    PASSWORDS_CLEAR,
    SAVE_PASSWORDS_MODIFY_RECEIVE,
} from '../actiontypes';
const defaultValue = '';

const password2Reducer = createReducer(defaultValue, {
    [MODIFY_PASSWORD2]: (state, action) => action.payload,
    [PASSWORDS_CLEAR]: () => defaultValue,
    [SAVE_PASSWORDS_MODIFY_RECEIVE]: () => defaultValue,
});

export default password2Reducer;
