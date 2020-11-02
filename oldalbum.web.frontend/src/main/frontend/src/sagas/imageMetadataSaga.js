import { takeLatest, call, put, select } from 'redux-saga/effects';
import axios from 'axios';
import {
    IMAGE_LOADED,
    IMAGE_METADATA,
    IMAGE_METADATA_ERROR,
} from '../reduxactions';

function doDownloadImageMetadata(url) {
    return axios.post('/api/image/metadata', { url });
}

function* downloadImageMetadata(action) {
    try {
        const url = action.payload;
        const response = yield call(doDownloadImageMetadata, url);
        const metadata = (response.headers['content-type'] === 'application/json') ? response.data : {};
        yield put(IMAGE_METADATA(metadata));
    } catch (error) {
        yield put(IMAGE_METADATA_ERROR(error));
    }
}

export default function* imageMetadataSaga() {
    yield takeLatest(IMAGE_LOADED,  downloadImageMetadata);
}
