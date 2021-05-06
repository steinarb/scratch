import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    MOVE_ALBUMENTRY_UP,
    MOVE_ALBUMENTRY_LEFT,
    MOVE_ALBUMENTRY_DOWN,
    MOVE_ALBUMENTRY_RIGHT,
    ALLROUTES_RECEIVE,
    MOVE_ALBUMENTRY_ERROR,
} from '../reduxactions';
import { stripFieldsNotInAlbumEntryJavaBean } from './commonSagaCode';

function moveAlbumentryUp(albumentry) {
    const body = stripFieldsNotInAlbumEntryJavaBean(albumentry);
    return axios.post('/api/movealbumentryup', body);
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
    return axios.post('/api/movealbumentrydown', body);
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
    yield takeLatest(MOVE_ALBUMENTRY_LEFT, moveAlbumentryUpAndReceiveRoutes);
    yield takeLatest(MOVE_ALBUMENTRY_DOWN, moveAlbumentryDownAndReceiveRoutes);
    yield takeLatest(MOVE_ALBUMENTRY_RIGHT, moveAlbumentryDownAndReceiveRoutes);
}
