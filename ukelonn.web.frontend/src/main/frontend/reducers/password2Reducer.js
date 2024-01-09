import { createReducer } from '@reduxjs/toolkit';
import {
    MODIFY_PASSWORD2,
    CLEAR_USER_AND_PASSWORDS,
} from '../actiontypes';
const defaultValue = '';

const password2Reducer = createReducer(defaultValue, builder => {
    builder
        .addCase(MODIFY_PASSWORD2, (state, action) => action.payload)
        .addCase(CLEAR_USER_AND_PASSWORDS, () => defaultValue);
});

export default password2Reducer;
