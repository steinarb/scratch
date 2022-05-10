import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    MOVE_ALBUMENTRY_UP_REQUEST,
    MOVE_ALBUMENTRY_UP_RECEIVE,
    MOVE_ALBUMENTRY_UP_FAILURE,
    MOVE_ALBUMENTRY_LEFT_REQUEST,
    MOVE_ALBUMENTRY_LEFT_RECEIVE,
    MOVE_ALBUMENTRY_LEFT_FAILURE,
    MOVE_ALBUMENTRY_DOWN_REQUEST,
    MOVE_ALBUMENTRY_DOWN_RECEIVE,
    MOVE_ALBUMENTRY_DOWN_FAILURE,
    MOVE_ALBUMENTRY_RIGHT_REQUEST,
    MOVE_ALBUMENTRY_RIGHT_RECEIVE,
    MOVE_ALBUMENTRY_RIGHT_FAILURE,
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
        yield put(MOVE_ALBUMENTRY_UP_RECEIVE(routes));
    } catch (error) {
        yield put(MOVE_ALBUMENTRY_UP_FAILURE(error));
    }
}


function* moveAlbumentryLeftAndReceiveRoutes(action) {
    try {
        const response = yield call(moveAlbumentryUp, action.payload);
        const routes = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(MOVE_ALBUMENTRY_LEFT_RECEIVE(routes));
    } catch (error) {
        yield put(MOVE_ALBUMENTRY_LEFT_FAILURE(error));
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
        yield put(MOVE_ALBUMENTRY_DOWN_RECEIVE(routes));
    } catch (error) {
        yield put(MOVE_ALBUMENTRY_DOWN_FAILURE(error));
    }
}

function* moveAlbumentryRightAndReceiveRoutes(action) {
    try {
        const response = yield call(moveAlbumentryDown, action.payload);
        const routes = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(MOVE_ALBUMENTRY_RIGHT_RECEIVE(routes));
    } catch (error) {
        yield put(MOVE_ALBUMENTRY_RIGHT_FAILURE(error));
    }
}

export default function* allroutesSaga() {
    yield takeLatest(MOVE_ALBUMENTRY_UP_REQUEST, moveAlbumentryUpAndReceiveRoutes);
    yield takeLatest(MOVE_ALBUMENTRY_LEFT_REQUEST, moveAlbumentryLeftAndReceiveRoutes);
    yield takeLatest(MOVE_ALBUMENTRY_DOWN_REQUEST, moveAlbumentryDownAndReceiveRoutes);
    yield takeLatest(MOVE_ALBUMENTRY_RIGHT_REQUEST, moveAlbumentryRightAndReceiveRoutes);
}
