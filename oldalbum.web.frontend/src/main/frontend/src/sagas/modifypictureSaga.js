import { takeLatest, call, put, select } from 'redux-saga/effects';
import axios from 'axios';
import {
    MODIFY_PICTURE_UPDATE,
    ALLROUTES_RECEIVE,
    MODIFY_PICTURE_ERROR,
} from '../reduxactions';
import { stripFieldsNotInAlbumEntryJavaBean } from './commonSagaCode';

function updateModifiedPicture(picture) {
    const body = stripFieldsNotInAlbumEntryJavaBean(picture);
    return axios.post('/api/modifypicture', body);
}

function* updatePictureAndReceiveRoutes(action) {
    try {
        const modifypicture = yield select(state => state.modifypicture);
        const response = yield call(updateModifiedPicture, modifypicture);
        const routes = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(ALLROUTES_RECEIVE(routes));
    } catch (error) {
        yield put(MODIFY_PICTURE_ERROR(error));
    }
}

export default function* allroutesSaga() {
    yield takeLatest(MODIFY_PICTURE_UPDATE, updatePictureAndReceiveRoutes);
}
