import { createSlice } from '@reduxjs/toolkit';
import { isUsersLoaded } from '../matchers';
import { api } from '../api';
import { emptyUser } from '../constants';

const initialState = {
    excessiveFailedLoginLimit: '',
};

export const configSlice = createSlice({
    name: 'config',
    initialState,
    reducers: {
        setConfig: (_, action) => action.payload,
        setExcessiveFailedLoginLimit: (state, action) => ({ ...state, excessiveFailedLoginLimit: action.payload }),
    },
});

export const { setConfig, setExcessiveFailedLoginLimit } = configSlice.actions;

export default configSlice.reducer;
