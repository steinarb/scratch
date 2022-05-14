import { takeLatest, call, put, select } from 'redux-saga/effects';
import axios from 'axios';
import {
    COUNTER_REQUEST,
    COUNTER_RECEIVE,
    COUNTER_ERROR,
    COUNTER_DECREMENT_REQUEST,
    COUNTER_DECREMENT_RECEIVE,
    COUNTER_DECREMENT_ERROR,
    COUNTER_INCREMENT_REQUEST,
    COUNTER_INCREMENT_RECEIVE,
    COUNTER_INCREMENT_ERROR,
    LOGIN_RECEIVE,
    LOGINSTATE_RECEIVE,
} from '../reduxactions';

function getCounter(username) {
    return axios.get('/api/counter/' + username);
}

function* fetchCounter() {
    try {
        const { success, authorized, user } = yield select(state => state.loginresult);
        const { username } = user;
        if (success && authorized) {
            const response = yield call(getCounter, username);
            const counterresult = (response.headers['content-type'] === 'application/json') ? response.data : {};
            yield put(COUNTER_RECEIVE(counterresult));
        }
    } catch (error) {
        yield put(COUNTER_ERROR(error));
    }
}

function getDecrementCounter(username) {
    return axios.get('/api/counter/' + username + '/decrement');
}

function* decrementCounter() {
    try {
        const { success, authorized, user } = yield select(state => state.loginresult);
        const { username } = user;
        if (success && authorized) {
            const response = yield call(getDecrementCounter, username);
            const counterresult = (response.headers['content-type'] === 'application/json') ? response.data : {};
            yield put(COUNTER_DECREMENT_RECEIVE(counterresult));
        }
    } catch (error) {
        yield put(COUNTER_DECREMENT_ERROR(error));
    }
}


function getIncrementCounter(username) {
    return axios.get('/api/counter/' + username + '/increment');
}

function* incrementCounter() {
    try {
        const { success, authorized, user } = yield select(state => state.loginresult);
        const { username } = user;
        if (success && authorized) {
            const response = yield call(getIncrementCounter, username);
            const counterresult = (response.headers['content-type'] === 'application/json') ? response.data : {};
            yield put(COUNTER_INCREMENT_RECEIVE(counterresult));
        }
    } catch (error) {
        yield put(COUNTER_INCREMENT_ERROR(error));
    }
}

export default function* counterSaga() {
    yield takeLatest(COUNTER_REQUEST, fetchCounter);
    yield takeLatest(LOGIN_RECEIVE, fetchCounter);
    yield takeLatest(LOGINSTATE_RECEIVE, fetchCounter);
    yield takeLatest(COUNTER_DECREMENT_REQUEST, decrementCounter);
    yield takeLatest(COUNTER_INCREMENT_REQUEST, incrementCounter);
}
