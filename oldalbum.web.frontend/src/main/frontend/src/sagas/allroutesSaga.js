import { takeLatest, call, put, select } from 'redux-saga/effects';
import axios from 'axios';
import { ALLROUTES_REQUEST, ALLROUTES_RECEIVE, ALLROUTES_ERROR } from '../reduxactions';

function fetchAllRoutes() {
    return axios.get('/oldalbum/api/allroutes');
}

function* receiveRoutes(action) {
    try {
        const response = yield call(fetchAllRoutes);
        const routes = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(ALLROUTES_RECEIVE(routes));
    } catch (error) {
        yield put(ALLROUTES_ERROR(error));
    }
}

export default function* allroutesSaga() {
    yield takeLatest(ALLROUTES_REQUEST, receiveRoutes);
}
