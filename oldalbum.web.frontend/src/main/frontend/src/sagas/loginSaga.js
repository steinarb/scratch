import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    LOGIN_REQUEST,
    LOGIN_RECEIVE,
    LOGIN_ERROR,
} from '../reduxactions';

function sendLogin(credentials) {
    return axios.post('/oldalbum/api/login', credentials);
}

function* receiveLoginResult(action) {
    try {
        const response = yield call(sendLogin, action.payload);
        const loginresult = (response.headers['content-type'] === 'application/json') ? response.data : {};
        yield put(LOGIN_RECEIVE(loginresult));
    } catch (error) {
        yield put(LOGIN_ERROR(error));
    }
}

export default function* loginSaga() {
    yield takeLatest(LOGIN_REQUEST, receiveLoginResult);
}
