import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    LOGIN_CHECK_REQUEST,
    LOGIN_CHECK_RECEIVE,
    LOGIN_CHECK_FAILURE,
} from '../reduxactions';

export default function* loginSaga() {
    yield takeLatest(LOGIN_CHECK_REQUEST, receiveCheckLoginResult);
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

function checkLogin() {
    return axios.get('/api/login');
}
