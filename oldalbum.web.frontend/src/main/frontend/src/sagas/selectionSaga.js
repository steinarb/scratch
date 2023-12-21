import { takeLatest, put, delay } from 'redux-saga/effects';
import {
    START_SELECTION_DOWNLOAD,
    CLEAR_SELECTION,
} from '../reduxactions';

function* clearDownloadSelection() {
    yield delay(1000);
    yield put(CLEAR_SELECTION());
}

export default function* selectionSaga() {
    yield takeLatest(START_SELECTION_DOWNLOAD, clearDownloadSelection);
}
