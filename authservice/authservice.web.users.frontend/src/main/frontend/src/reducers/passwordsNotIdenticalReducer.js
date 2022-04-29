import { createReducer } from '@reduxjs/toolkit';
import {
    MODIFY_PASSWORDS_NOT_IDENTICAL,
    PASSWORDS_CLEAR,
    SAVE_PASSWORDS_MODIFY_RECEIVE,
} from '../actiontypes';
const defaultValue = false;

const passwordsNotIdenticalReducer = createReducer(defaultValue, {
    [MODIFY_PASSWORDS_NOT_IDENTICAL]: (state, action) => action.payload,
    [PASSWORDS_CLEAR]: () => defaultValue,
    [SAVE_PASSWORDS_MODIFY_RECEIVE]: () => defaultValue,
});

export default passwordsNotIdenticalReducer;
