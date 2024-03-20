import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import { RELOAD_SHIRO_CONFIG_REQUEST, RELOAD_SHIRO_CONFIG_RECEIVE, RELOAD_SHIRO_CONFIG_FAILURE } from '../reduxactions';

export default function* reloadShiroConfigSaga() {
    yield takeLatest(RELOAD_SHIRO_CONFIG_REQUEST, receiveConfigReloadResult);
}

function* receiveConfigReloadResult() {
    try {
        const response = yield call(fetchReloadShiroConfig);
        const routes = (response.headers['content-type'] === 'txt/plain') ? response.data : [];
        yield put(RELOAD_SHIRO_CONFIG_RECEIVE(routes));
    } catch (error) {
        yield put(RELOAD_SHIRO_CONFIG_FAILURE(error));
    }
}

function fetchReloadShiroConfig() {
    return axios.get('/api/reloadshiroconfig');
}
