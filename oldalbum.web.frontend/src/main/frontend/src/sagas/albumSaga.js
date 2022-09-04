import { takeLatest, put, select } from 'redux-saga/effects';
import { push, goBack } from 'redux-first-history';
import {
    MODIFY_ALBUM_UPDATE_BUTTON_CLICKED,
    MODIFY_ALBUM_CANCEL_BUTTON_CLICKED,
    ADD_ALBUM_UPDATE_BUTTON_CLICKED,
    ADD_ALBUM_CANCEL_BUTTON_CLICKED,
    CLEAR_ALBUM_FORM,
    SAVE_MODIFIED_ALBUM_REQUEST,
    SAVE_MODIFIED_ALBUM_RECEIVE,
    SAVE_ADDED_ALBUM_REQUEST,
    SAVE_ADDED_ALBUM_RECEIVE,
} from '../reduxactions';

function* saveModifiedAlbum() {
    const album = yield select(state => ({
        id: state.albumentryid,
        parent: state.albumentryParent,
        path: state.albumentryPath,
        album: true,
        title: state.albumentryTitle,
        description: state.albumentryDescription,
        sort: state.albumentrySort,
    }));
    yield put(SAVE_MODIFIED_ALBUM_REQUEST(album));
}

function* saveAddedAlbum() {
    const album = yield select(state => ({
        parent: state.albumentryParent,
        path: state.albumentryPath,
        album: true,
        title: state.albumentryTitle,
        description: state.albumentryDescription,
        sort: state.albumentrySort,
    }));
    yield put(SAVE_ADDED_ALBUM_REQUEST(album));
}

function* clearFormAndNavigateToAlbum() {
    const basename = yield select(state => state.basename);
    const path = yield select(state => state.albumentryPath);
    yield put(CLEAR_ALBUM_FORM());
    yield put(push(basename + path));
}

function* clearFormAndNavigateBack() {
    yield put(CLEAR_ALBUM_FORM());
    yield put(goBack());
}

export default function* albumSaga() {
    yield takeLatest(MODIFY_ALBUM_UPDATE_BUTTON_CLICKED, saveModifiedAlbum);
    yield takeLatest(ADD_ALBUM_UPDATE_BUTTON_CLICKED, saveAddedAlbum);
    yield takeLatest(SAVE_MODIFIED_ALBUM_RECEIVE, clearFormAndNavigateToAlbum);
    yield takeLatest(SAVE_ADDED_ALBUM_RECEIVE, clearFormAndNavigateToAlbum);
    yield takeLatest(MODIFY_ALBUM_CANCEL_BUTTON_CLICKED, clearFormAndNavigateBack);
    yield takeLatest(ADD_ALBUM_CANCEL_BUTTON_CLICKED, clearFormAndNavigateBack);
}
