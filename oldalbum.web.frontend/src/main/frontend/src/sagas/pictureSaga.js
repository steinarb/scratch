import { takeLatest, put, select } from 'redux-saga/effects';
import { push, goBack } from 'redux-first-history';
import {
    MODIFY_PICTURE_UPDATE_BUTTON_CLICKED,
    MODIFY_PICTURE_CANCEL_BUTTON_CLICKED,
    ADD_PICTURE_UPDATE_BUTTON_CLICKED,
    ADD_PICTURE_CANCEL_BUTTON_CLICKED,
    CLEAR_PICTURE_FORM,
    SAVE_MODIFIED_PICTURE_REQUEST,
    SAVE_MODIFIED_PICTURE_RECEIVE,
    SAVE_ADDED_PICTURE_REQUEST,
    SAVE_ADDED_PICTURE_RECEIVE,
} from '../reduxactions';

function* saveModifiedPicture() {
    const picture = yield select(state => ({
        id: state.albumentryid,
        parent: state.albumentryParent,
        path: state.albumentryPath,
        album: false,
        title: state.albumentryTitle,
        description: state.albumentryDescription,
        imageUrl: state.albumentryImageUrl,
        thumbnailUrl: state.albumentryThumbnailUrl,
        sort: state.albumentrySort,
    }));
    yield put(SAVE_MODIFIED_PICTURE_REQUEST(picture));
}

function* saveAddedPicture() {
    const picture = yield select(state => ({
        id: state.albumentryid,
        parent: state.albumentryParent,
        path: state.albumentryPath,
        album: false,
        title: state.albumentryTitle,
        description: state.albumentryDescription,
        imageUrl: state.albumentryImageUrl,
        thumbnailUrl: state.albumentryThumbnailUrl,
        sort: state.albumentrySort,
    }));
    yield put(SAVE_ADDED_PICTURE_REQUEST(picture));
}

function* clearFormAndNavigateToPicture() {
    const basename = yield select(state => state.basename);
    const path = yield select(state => state.albumentryPath);
    yield put(CLEAR_PICTURE_FORM());
    yield put(push(basename + path));
}

function* clearFormAndNavigateBack() {
    yield put(CLEAR_PICTURE_FORM());
    yield put(goBack());
}

export default function* pictureSaga() {
    yield takeLatest(MODIFY_PICTURE_UPDATE_BUTTON_CLICKED, saveModifiedPicture);
    yield takeLatest(ADD_PICTURE_UPDATE_BUTTON_CLICKED, saveAddedPicture);
    yield takeLatest(SAVE_MODIFIED_PICTURE_RECEIVE, clearFormAndNavigateToPicture);
    yield takeLatest(SAVE_ADDED_PICTURE_RECEIVE, clearFormAndNavigateToPicture);
    yield takeLatest(MODIFY_PICTURE_CANCEL_BUTTON_CLICKED, clearFormAndNavigateBack);
    yield takeLatest(ADD_PICTURE_CANCEL_BUTTON_CLICKED, clearFormAndNavigateBack);
}
