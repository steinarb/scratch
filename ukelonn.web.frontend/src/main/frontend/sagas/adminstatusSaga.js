import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    REQUEST_ADMIN_STATUS,
    RECEIVE_ADMIN_STATUS,
    RECEIVE_ADMIN_STATUS_ERROR,
} from '../actiontypes';

function* requestAdminStatus(action) {
    const user = action.payload;
    try {
        const response = yield call(fetchAdminStatus, user);
        const adminStatus = (response.headers['content-type'] === 'application/json') ? response.data : {};
        yield put(RECEIVE_ADMIN_STATUS(adminStatus));
    } catch (error) {
        yield put(RECEIVE_ADMIN_STATUS_ERROR(error));
    }
}

function fetchAdminStatus(user) {
    return axios.post('/ukelonn/api/admin/user/adminstatus', user);
}

export default function* adminStatusSaga() {
    yield takeLatest(REQUEST_ADMIN_STATUS, requestAdminStatus);
}
