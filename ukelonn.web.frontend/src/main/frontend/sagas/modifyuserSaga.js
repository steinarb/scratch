import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    MODIFY_USER_REQUEST,
    MODIFY_USER_RECEIVE,
    MODIFY_USER_FAILURE,
} from '../actiontypes';

// watcher saga
export function* requestModifyUserSaga() {
    yield takeLatest(MODIFY_USER_REQUEST, receiveModifyUserSaga);
}

function doModifyUser(user) {
    return axios.post('/ukelonn/api/admin/user/modify', user);
}

// worker saga
function* receiveModifyUserSaga(action) {
    try {
        const response = yield call(doModifyUser, action.payload);
        const users = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(MODIFY_USER_RECEIVE(users));
    } catch (error) {
        yield put(MODIFY_USER_FAILURE(error));
    }
}
