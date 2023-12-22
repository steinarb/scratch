import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    DELETE_ALBUMENTRY_REQUEST,
    DELETE_ALBUMENTRY_RECEIVE,
    DELETE_ALBUMENTRY_FAILURE,
} from '../reduxactions';

function deleteAlbumItem(item) {
    return axios.post('/api/deleteentry', item);
}

function* deleteItemAndReceiveRoutes(action) {
    try {
        const response = yield call(deleteAlbumItem, action.payload);
        const routes = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(DELETE_ALBUMENTRY_RECEIVE(routes));
    } catch (error) {
        yield put(DELETE_ALBUMENTRY_FAILURE(error));
    }
}

export default function* deleteSaga() {
    yield takeLatest(DELETE_ALBUMENTRY_REQUEST, deleteItemAndReceiveRoutes);
}
