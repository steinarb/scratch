import { createReducer } from '@reduxjs/toolkit';
import {
    MODIFY_PASSWORD1,
    PASSWORDS_CLEAR,
    SAVE_PASSWORDS_MODIFY_RECEIVE,
} from '../actiontypes';
const defaultValue = '';

const password1Reducer = createReducer(defaultValue, {
    [MODIFY_PASSWORD1]: (state, action) => action.payload,
    [PASSWORDS_CLEAR]: () => defaultValue,
    [SAVE_PASSWORDS_MODIFY_RECEIVE]: () => defaultValue,
});

export default password1Reducer;
