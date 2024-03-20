import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    SORT_ALBUM_ENTRIES_BY_DATE_REQUEST,
    SORT_ALBUM_ENTRIES_BY_DATE_RECEIVE,
    SORT_ALBUM_ENTRIES_BY_DATE_FAILURE,
} from '../reduxactions';

export default function* addPictureSaga() {
    yield takeLatest(SORT_ALBUM_ENTRIES_BY_DATE_REQUEST, sortAlbumAndReceiveRoutes);
}

function* sortAlbumAndReceiveRoutes(action) {
    try {
        const response = yield call(sortAlbumentriesByDate, action.payload);
        const routes = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(SORT_ALBUM_ENTRIES_BY_DATE_RECEIVE(routes));
    } catch (error) {
        yield put(SORT_ALBUM_ENTRIES_BY_DATE_FAILURE(error));
    }
}

function sortAlbumentriesByDate(batchaddpicturesrequest) {
    return axios.post('/api/sortalbumbydate', batchaddpicturesrequest);
}
