import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    SAVE_MODIFIED_ALBUM_REQUEST,
    SAVE_MODIFIED_ALBUM_RECEIVE,
    SAVE_MODIFIED_ALBUM_FAILURE,
} from '../reduxactions';

function postModifiedAlbum(album) {
    return axios.post('/api/modifyalbum', album);
}

function* saveModifiedAlbum(action) {
    try {
        const response = yield call(postModifiedAlbum, action.payload);
        const routes = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(SAVE_MODIFIED_ALBUM_RECEIVE(routes));
    } catch (error) {
        yield put(SAVE_MODIFIED_ALBUM_FAILURE(error));
    }
}

export default function* modifyAlbumSaga() {
    yield takeLatest(SAVE_MODIFIED_ALBUM_REQUEST, saveModifiedAlbum);
}
