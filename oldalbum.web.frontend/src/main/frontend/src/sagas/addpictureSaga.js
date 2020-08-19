import { takeLatest, call, put, select } from 'redux-saga/effects';
import axios from 'axios';
import {
    ADD_PICTURE_UPDATE,
    ALLROUTES_RECEIVE,
    ADD_PICTURE_ERROR,
} from '../reduxactions';
import { stripFieldsNotInAlbumEntryJavaBean } from './commonSagaCode';

function updateAddedPicture(picture, webcontext) {
    const body = stripFieldsNotInAlbumEntryJavaBean(picture);
    return axios.post('/api/addpicture', body);
}

function* updatePictureAndReceiveRoutes(action) {
    try {
        const webcontext = yield select(state => state.webcontext);
        const addpicture = yield select(state => state.addpicture);
        const response = yield call(updateAddedPicture, addpicture, webcontext);
        const routes = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(ALLROUTES_RECEIVE(routes));
    } catch (error) {
        yield put(ADD_PICTURE_ERROR(error));
    }
}

export default function* allroutesSaga() {
    yield takeLatest(ADD_PICTURE_UPDATE, updatePictureAndReceiveRoutes);
}
