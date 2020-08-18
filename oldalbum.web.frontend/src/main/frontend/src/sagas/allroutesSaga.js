import { takeLatest, call, put, select } from 'redux-saga/effects';
import axios from 'axios';
import { ALLROUTES_REQUEST, ALLROUTES_RECEIVE, ALLROUTES_ERROR } from '../reduxactions';
import { addWebcontextToPath } from '../common';

function fetchAllRoutes() {
    return axios.get('/oldalbum/api/allroutes');
}

function* receiveRoutes(action) {
    try {
        const response = yield call(fetchAllRoutes);
        const routes = (response.headers['content-type'] === 'application/json') ? response.data : [];
        const webcontext = yield select(state => state.webcontext);
        const routesWithWebcontext = routes.map(r => addWebcontextToPath(r, webcontext));
        yield put(ALLROUTES_RECEIVE(routesWithWebcontext));
    } catch (error) {
        yield put(ALLROUTES_ERROR(error));
    }
}

export default function* allroutesSaga() {
    yield takeLatest(ALLROUTES_REQUEST, receiveRoutes);
}
