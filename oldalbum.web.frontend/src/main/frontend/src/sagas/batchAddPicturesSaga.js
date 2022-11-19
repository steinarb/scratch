import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    BATCH_ADD_PICTURES_FROM_URL,
    INDEX_HTML_OF_PICTURE_DIRECTORY_RECEIVE,
    INDEX_HTML_OF_PICTURE_DIRECTORY_FAILURE,
} from '../reduxactions';

function updateAddedPicture(picture) {
    return axios.post('/api/addpicture', picture);
}

function* updatePictureAndReceiveRoutes(action) {
    try {
        const response = yield call(updateAddedPicture, action.payload);
        const routes = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(INDEX_HTML_OF_PICTURE_DIRECTORY_RECEIVE(routes));
    } catch (error) {
        yield put(INDEX_HTML_OF_PICTURE_DIRECTORY_FAILURE(error));
    }
}

export default function* addPictureSaga() {
    yield takeLatest(BATCH_ADD_PICTURES_FROM_URL, updatePictureAndReceiveRoutes);
}
