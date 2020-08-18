import { takeLatest, call, put, select } from 'redux-saga/effects';
import axios from 'axios';
import {
    MODIFY_PICTURE_UPDATE,
    ALLROUTES_RECEIVE,
    MODIFY_PICTURE_ERROR,
} from '../reduxactions';
import { removeWebcontextFromPath } from '../common';
import { stripFieldsNotInAlbumEntryJavaBean } from './commonSagaCode';

function updateModifiedPicture(picture, webcontext) {
    const body = removeWebcontextFromPath(stripFieldsNotInAlbumEntryJavaBean(picture), webcontext);
    return axios.post('/oldalbum/api/modifypicture', body);
}

function* updatePictureAndReceiveRoutes(action) {
    try {
        const webcontext = yield select(state => state.webcontext);
        const modifypicture = yield select(state => state.modifypicture);
        const response = yield call(updateModifiedPicture, modifypicture, webcontext);
        const routes = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(ALLROUTES_RECEIVE(routes));
    } catch (error) {
        yield put(MODIFY_PICTURE_ERROR(error));
    }
}

export default function* allroutesSaga() {
    yield takeLatest(MODIFY_PICTURE_UPDATE, updatePictureAndReceiveRoutes);
}
