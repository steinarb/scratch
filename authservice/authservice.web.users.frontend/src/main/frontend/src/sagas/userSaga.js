import { takeLatest, select, put } from 'redux-saga/effects';
import {
    SELECT_USER,
    SELECTED_USER,
} from '../actiontypes';

function* selectedUser(action) {
    const users = yield select(state => state.users);
    const user = users.find(u => u.userid === action.payload);
    if (user) {
        yield put(SELECTED_USER(user));
    }
}

export default function* userSaga() {
    yield takeLatest(SELECT_USER, selectedUser);
}
