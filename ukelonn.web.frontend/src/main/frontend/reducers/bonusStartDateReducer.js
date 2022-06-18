import { createReducer } from '@reduxjs/toolkit';
import {
    MODIFY_BONUS_START_DATE,
    SELECTED_BONUS,
    CLEAR_BONUS,
} from '../actiontypes';
import { isUnselected } from '../common/reducers';

const bonusStartDateReducer = createReducer(new Date().toISOString(), {
    [MODIFY_BONUS_START_DATE]: (state, action) => action.payload + 'T' + state.split('T')[1],
    [SELECTED_BONUS]: (state, action) => isUnselected(action.payload.bonusId) ? new Date().toISOString() : new Date(action.payload.startDate).toISOString(),
    [CLEAR_BONUS]: () => new Date().toISOString(),
});

export default bonusStartDateReducer;
