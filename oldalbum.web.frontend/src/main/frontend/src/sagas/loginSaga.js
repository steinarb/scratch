import { takeLatest, call, put, select } from 'redux-saga/effects';
import axios from 'axios';
import {
    LOGIN_CHECK_REQUEST,
    LOGIN_CHECK_RECEIVE,
    LOGIN_CHECK_FAILURE,
    LOGIN_REQUEST,
    LOGIN_RECEIVE,
    LOGIN_FAILURE,
    ALLROUTES_REQUEST,
} from '../reduxactions';

function checkLogin() {
    return axios.get('/api/login');
}

function sendLogin(credentials, locale) {
    return axios.post('/api/login', credentials, { params: { locale } });
}

function* receiveCheckLoginResult() {
    try {
        const response = yield call(checkLogin);
        const loginresult = (response.headers['content-type'] === 'application/json') ? response.data : {};
        yield put(LOGIN_CHECK_RECEIVE(loginresult));
    } catch (error) {
        console.log('error');
        console.log(error);
        yield put(LOGIN_CHECK_FAILURE(error));
    }
}

function* receiveLoginResult(action) {
    try {
        const locale = yield select(state => state.locale);
        const response = yield call(sendLogin, action.payload, locale);
        const loginresult = (response.headers['content-type'] === 'application/json') ? response.data : {};
        yield put(LOGIN_RECEIVE(loginresult));
        if (loginresult.success) {
            yield put(ALLROUTES_REQUEST());
        }
    } catch (error) {
        yield put(LOGIN_FAILURE(error));
    }
}

export default function* loginSaga() {
    yield takeLatest(LOGIN_CHECK_REQUEST, receiveCheckLoginResult);
    yield takeLatest(LOGIN_REQUEST, receiveLoginResult);
}
