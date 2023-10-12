import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    IMAGE_METADATA_REQUEST,
    IMAGE_METADATA_RECEIVE,
    IMAGE_METADATA_FAILURE,
    ADD_PICTURE_BASENAME_FIELD_CHANGED,
} from '../reduxactions';

function doDownloadImageMetadata(url) {
    return axios.post('/api/image/metadata', { url });
}

function* downloadImageMetadata(action) {
    try {
        const url = action.payload;
        const response = yield call(doDownloadImageMetadata, url);
        const metadata = (response.headers['content-type'] === 'application/json') ? response.data : {};
        yield put(IMAGE_METADATA_RECEIVE(metadata));
    } catch (error) {
        yield put(IMAGE_METADATA_FAILURE(error));
    }
}

function* extractBasename(action) {
    const url = action.payload;
    const paths = url.split('/');
    const filename = paths.pop();
    const fileAndExtension = filename.split('.');
    const basename = fileAndExtension.shift();
    yield put(ADD_PICTURE_BASENAME_FIELD_CHANGED(basename));
}

export default function* imageMetadataSaga() {
    yield takeLatest(IMAGE_METADATA_REQUEST,  downloadImageMetadata);
    yield takeLatest(IMAGE_METADATA_REQUEST,  extractBasename);
}
