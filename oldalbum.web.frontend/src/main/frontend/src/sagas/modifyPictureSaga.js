import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    SAVE_MODIFIED_PICTURE_REQUEST,
    SAVE_MODIFIED_PICTURE_RECEIVE,
    SAVE_MODIFIED_PICTURE_FAILURE,
} from '../reduxactions';

function postModifiedPicture(picture) {
    return axios.post('/api/modifypicture', picture);
}

function* saveModifiedPicture(action) {
    try {
        const response = yield call(postModifiedPicture, action.payload);
        const routes = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(SAVE_MODIFIED_PICTURE_RECEIVE(routes));
    } catch (error) {
        yield put(SAVE_MODIFIED_PICTURE_FAILURE(error));
    }
}

export default function* modifyPictureSaga() {
    yield takeLatest(SAVE_MODIFIED_PICTURE_REQUEST, saveModifiedPicture);
}
