import { createSlice } from '@reduxjs/toolkit';

export const counterIncrementStepSlice = createSlice({
    name: 'counterIncrementStep',
    1,
    reducers: {
        setIncrementStep: (_, action) => parseInt(action.payload) || 0,
    },
    extraReducers: builder => {
        builder
            .addMatcher(api.endpoints.getCounterIncrementStep.matchFulfilled, (_, action) => action.payload.counterIncrementStep)
});

export const { setIncrementStep } = counterIncrementStepSlice.actions;

export default counterIncrementStepSlice.reducer;
