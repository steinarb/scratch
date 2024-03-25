import { takeLatest, select, put, delay } from 'redux-saga/effects';
import {
    ALBUM_SELECT_ALL,
    SET_SELECTION_VALUE,
    START_SELECTION_DOWNLOAD,
    DELETE_SELECTION_RECEIVE,
    CLEAR_SELECTION,
} from '../reduxactions';

export default function* selectionSaga() {
    yield takeLatest(ALBUM_SELECT_ALL, selectAllAlbumEntries);
    yield takeLatest(START_SELECTION_DOWNLOAD, clearDownloadSelection);
    yield takeLatest(DELETE_SELECTION_RECEIVE, clearDownloadSelection);
}

function* selectAllAlbumEntries(action) {
    const album = action.payload;
    const allroutes = yield select(state => state.allroutes);
    const picturesInAlbum = allroutes.filter(r => r.parent === album.id).filter(r => !r.album);
    yield put(SET_SELECTION_VALUE(picturesInAlbum));
}

function* clearDownloadSelection() {
    yield delay(1000);
    yield put(CLEAR_SELECTION());
}
