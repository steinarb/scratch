import { takeLatest, put, delay } from 'redux-saga/effects';
import {
    START_SELECTION_DOWNLOAD,
    DELETE_SELECTION_RECEIVE,
    CLEAR_SELECTION,
} from '../reduxactions';

function* clearDownloadSelection() {
    yield delay(1000);
    yield put(CLEAR_SELECTION());
}

export default function* selectionSaga() {
    yield takeLatest(START_SELECTION_DOWNLOAD, clearDownloadSelection);
    yield takeLatest(DELETE_SELECTION_RECEIVE, clearDownloadSelection);
}
