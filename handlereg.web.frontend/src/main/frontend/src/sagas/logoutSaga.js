import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    LOGOUT_HENT,
    LOGOUT_MOTTA,
    LOGOUT_ERROR,
} from '../actiontypes';

function sendLogout() {
    return axios.get('/handlereg/api/logout');
}

function* mottaLogoutResultat(action) {
    try {
        const response = yield call(sendLogout);
        const logoutresult = (response.headers['content-type'] === 'application/json') ? response.data : {};
        yield put(LOGOUT_MOTTA(logoutresult));
    } catch (error) {
        yield put(LOGOUT_ERROR(error));
    }
}

export default function* logoutSaga() {
    yield takeLatest(LOGOUT_HENT, mottaLogoutResultat);
}
