import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import { ALLROUTES_REQUEST, ALLROUTES_RECEIVE, ALLROUTES_FAILURE } from '../reduxactions';

function fetchAllRoutes() {
    return axios.get('/api/allroutes');
}

function* receiveRoutes() {
    try {
        const response = yield call(fetchAllRoutes);
        const routes = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(ALLROUTES_RECEIVE(routes));
    } catch (error) {
        yield put(ALLROUTES_FAILURE(error));
    }
}

export default function* allroutesSaga() {
    yield takeLatest(ALLROUTES_REQUEST, receiveRoutes);
}
