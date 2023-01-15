import { takeLatest, call, put, select } from 'redux-saga/effects';
import axios from 'axios';
import {
    LOGOUT_REQUEST,
    LOGOUT_RECEIVE,
    LOGOUT_FAILURE,
    ALLROUTES_REQUEST,
} from '../reduxactions';

function sendLogout(locale) {
    return axios.get('/api/logout', { params: { locale } });
}

function* receiveLogoutResult(action) {
    try {
        const locale = yield select(state => state.locale);
        const response = yield call(sendLogout, action.payload, locale);
        const logoutresult = (response.headers['content-type'] === 'application/json') ? response.data : {};
        yield put(LOGOUT_RECEIVE(logoutresult));
        if (!logoutresult.success) {
            yield put(ALLROUTES_REQUEST());
        }
    } catch (error) {
        yield put(LOGOUT_FAILURE(error));
    }
}

export default function* logoutSaga() {
    yield takeLatest(LOGOUT_REQUEST, receiveLogoutResult);
}
