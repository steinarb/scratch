import { takeLatest, call, put, select } from 'redux-saga/effects';
import axios from 'axios';
import {
    MOVE_ALBUMENTRY_UP,
    MOVE_ALBUMENTRY_DOWN,
    ALLROUTES_RECEIVE,
    MOVE_ALBUMENTRY_ERROR,
} from '../reduxactions';
import { stripFieldsNotInAlbumEntryJavaBean } from './commonSagaCode';

function moveAlbumentryUp(albumentry) {
    const body = stripFieldsNotInAlbumEntryJavaBean(albumentry);
    return axios.post('/oldalbum/api/movealbumentryup', body);
}

function* moveAlbumentryUpAndReceiveRoutes(action) {
    try {
        const response = yield call(moveAlbumentryUp, action.payload);
        const routes = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(ALLROUTES_RECEIVE(routes));
    } catch (error) {
        yield put(MOVE_ALBUMENTRY_ERROR(error));
    }
}

function moveAlbumentryDown(albumentry) {
    const body = stripFieldsNotInAlbumEntryJavaBean(albumentry);
    return axios.post('/oldalbum/api/movealbumentrydown', body);
}

function* moveAlbumentryDownAndReceiveRoutes(action) {
    try {
        const response = yield call(moveAlbumentryDown, action.payload);
        const routes = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(ALLROUTES_RECEIVE(routes));
    } catch (error) {
        yield put(MOVE_ALBUMENTRY_ERROR(error));
    }
}

export default function* allroutesSaga() {
    yield takeLatest(MOVE_ALBUMENTRY_UP, moveAlbumentryUpAndReceiveRoutes);
    yield takeLatest(MOVE_ALBUMENTRY_DOWN, moveAlbumentryDownAndReceiveRoutes);
}
