import { takeLatest, call, put, select } from 'redux-saga/effects';
import axios from 'axios';
import {
    ADD_ALBUM_UPDATE,
    ALLROUTES_RECEIVE,
    ADD_ALBUM_ERROR,
} from '../reduxactions';
import { removeWebcontextFromPath } from '../common';
import { stripFieldsNotInAlbumEntryJavaBean } from './commonSagaCode';

function updateAddedAlbum(album) {
    const body = removeWebcontextFromPath(stripFieldsNotInAlbumEntryJavaBean(album));
    return axios.post('/oldalbum/api/addalbum', body);
}

function* updateAlbumAndReceiveRoutes(action) {
    try {
        const addalbum = yield select(state => state.addalbum);
        const response = yield call(updateAddedAlbum, addalbum);
        const routes = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(ALLROUTES_RECEIVE(routes));
    } catch (error) {
        yield put(ADD_ALBUM_ERROR(error));
    }
}

export default function* allroutesSaga() {
    yield takeLatest(ADD_ALBUM_UPDATE, updateAlbumAndReceiveRoutes);
}
