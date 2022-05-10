import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    SAVE_ADDED_PICTURE_REQUEST,
    SAVE_ADDED_PICTURE_RECEIVE,
    SAVE_ADDED_PICTURE_FAILURE,
} from '../reduxactions';

function updateAddedPicture(picture) {
    return axios.post('/api/addpicture', picture);
}

function* updatePictureAndReceiveRoutes(action) {
    try {
        const response = yield call(updateAddedPicture, action.payload);
        const routes = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(SAVE_ADDED_PICTURE_RECEIVE(routes));
    } catch (error) {
        yield put(SAVE_ADDED_PICTURE_FAILURE(error));
    }
}

export default function* addPictureSaga() {
    yield takeLatest(SAVE_ADDED_PICTURE_REQUEST, updatePictureAndReceiveRoutes);
}
