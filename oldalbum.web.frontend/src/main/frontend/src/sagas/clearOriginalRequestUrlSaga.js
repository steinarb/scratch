import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    CLEAR_ORIGINAL_REQUEST_URL_REQUEST,
    CLEAR_ORIGINAL_REQUEST_URL_RECEIVE,
    CLEAR_ORIGINAL_REQUEST_URL_FAILURE,
} from '../reduxactions';

export default function* clearOriginalRequestUrlSaga() {
    yield takeLatest(CLEAR_ORIGINAL_REQUEST_URL_REQUEST, receiveClearOriginalRequestUrl);
}

function* receiveClearOriginalRequestUrl() {
    try {
        const response = yield call(checkLogin);
        const loginresult = (response.headers['content-type'] === 'application/json') ? response.data : {};
        yield put(CLEAR_ORIGINAL_REQUEST_URL_RECEIVE(loginresult));
    } catch (error) {
        console.log('error');
        console.log(error);
        yield put(CLEAR_ORIGINAL_REQUEST_URL_FAILURE(error));
    }
}

function checkLogin() {
    return axios.get('/api/clearoriginalrequesturl');
}
