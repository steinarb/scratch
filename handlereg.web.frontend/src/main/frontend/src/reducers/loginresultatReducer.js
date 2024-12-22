import { createReducer } from '@reduxjs/toolkit';
import { usePostLoginMutation } from '../api';
import { api } from '../api';

const loginresultatReducer = createReducer({ authorized: true }, builder => {
    builder
        .addMatcher(api.endpoints.postLogin.matchFulfilled, (state, action) => action.payload)
        .addMatcher(api.endpoints.getLogout.matchFulfilled, (state, action) => action.payload)
        .addMatcher(api.endpoints.getLogintilstand.matchFulfilled, (state, action) => action.payload);
});

export default loginresultatReducer;
