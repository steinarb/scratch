import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    SAVE_ADDED_ALBUM_REQUEST,
    SAVE_ADDED_ALBUM_RECEIVE,
    SAVE_ADDED_ALBUM_FAILURE,
} from '../reduxactions';

function postAddAlbum(album) {
    return axios.post('/api/addalbum', album);
}

function* requestAddAlbumAndReceiveRoutes(action) {
    try {
        const response = yield call(postAddAlbum, action.payload);
        const routes = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(SAVE_ADDED_ALBUM_RECEIVE(routes));
    } catch (error) {
        yield put(SAVE_ADDED_ALBUM_FAILURE(error));
    }
}

export default function* addAlbumSaga() {
    yield takeLatest(SAVE_ADDED_ALBUM_REQUEST, requestAddAlbumAndReceiveRoutes);
}
