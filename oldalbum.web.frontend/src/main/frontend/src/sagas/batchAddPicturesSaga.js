import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    BATCH_ADD_PICTURES_FROM_URL_REQUEST,
    BATCH_ADD_PICTURES_FROM_URL_RECEIVE,
    BATCH_ADD_PICTURES_FROM_URL_FAILURE,
} from '../reduxactions';

export default function* addPictureSaga() {
    yield takeLatest(BATCH_ADD_PICTURES_FROM_URL_REQUEST, batchAddPicturesAndReceiveRoutes);
}

function* batchAddPicturesAndReceiveRoutes(action) {
    try {
        const response = yield call(batchAddPictures, action.payload);
        const routes = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(BATCH_ADD_PICTURES_FROM_URL_RECEIVE(routes));
    } catch (error) {
        yield put(BATCH_ADD_PICTURES_FROM_URL_FAILURE(error));
    }
}

function batchAddPictures(batchaddpicturesrequest) {
    return axios.post('/api/batchaddpictures', batchaddpicturesrequest);
}
