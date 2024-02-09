import { takeLatest, select, put } from 'redux-saga/effects';
import {
    SAVE_MODIFIED_ALBUM_FAILURE,
    SAVE_ADDED_ALBUM_FAILURE,
    BATCH_ADD_PICTURES_FROM_URL_FAILURE,
    SAVE_MODIFIED_PICTURE_FAILURE,
    SAVE_ADDED_PICTURE_FAILURE,
    DELETE_ALBUMENTRY_FAILURE,
    DELETE_SELECTION_FAILURE,
    SORT_ALBUM_ENTRIES_BY_DATE_FAILURE,
    MOVE_ALBUMENTRY_UP_FAILURE,
    MOVE_ALBUMENTRY_DOWN_FAILURE,
    MOVE_ALBUMENTRY_LEFT_FAILURE,
    MOVE_ALBUMENTRY_RIGHT_FAILURE,
    SET_MODIFY_FAILED_ERROR,
} from '../reduxactions';

export default function* modifyFailedErrorSaga() {
    yield takeLatest(SAVE_MODIFIED_ALBUM_FAILURE, createErrorMessageForSaveModifiedAlbumFailed);
    yield takeLatest(SAVE_ADDED_ALBUM_FAILURE, createErrorMessageForSaveAddedAlbumFailed);
    yield takeLatest(BATCH_ADD_PICTURES_FROM_URL_FAILURE, createErrorMessageBatchAddPicturesFromUrlFailed);
    yield takeLatest(SAVE_MODIFIED_PICTURE_FAILURE, createErrorMessageForSaveModifiedPictureFailed);
    yield takeLatest(SAVE_ADDED_PICTURE_FAILURE, createErrorMessageForSaveAddedPictureFailed);
    yield takeLatest(DELETE_ALBUMENTRY_FAILURE, createErrorMessageForDeleteFailed);
    yield takeLatest(DELETE_SELECTION_FAILURE, createErrorMessageForDeleteFailed);
    yield takeLatest(SORT_ALBUM_ENTRIES_BY_DATE_FAILURE, createErrorMessageForSortFailed);
    yield takeLatest(MOVE_ALBUMENTRY_UP_FAILURE, createErrorMessageForMoveAlbumEntryUpFailed);
    yield takeLatest(MOVE_ALBUMENTRY_DOWN_FAILURE, createErrorMessageForMoveAlbumEntryDownFailed);
    yield takeLatest(MOVE_ALBUMENTRY_LEFT_FAILURE, createErrorMessageForMoveAlbumEntryLeftFailed);
    yield takeLatest(MOVE_ALBUMENTRY_RIGHT_FAILURE, createErrorMessageForMoveAlbumEntryRightFailed);
}

function* createErrorMessageForSaveModifiedAlbumFailed(action) {
    const text = yield select(state => state.displayTexts);
    const responseCode = action.payload.response.status;
    const reason = findReason(responseCode, text);
    const message = text.saveModifiedAlbumFailed + ': ' + reason;
    yield put(SET_MODIFY_FAILED_ERROR(message));
}

function* createErrorMessageForSaveAddedAlbumFailed(action) {
    const text = yield select(state => state.displayTexts);
    const responseCode = action.payload.response.status;
    const reason = findReason(responseCode, text);
    const message = text.saveAddedAlbumFailed + ': ' + reason;
    yield put(SET_MODIFY_FAILED_ERROR(message));
}

function* createErrorMessageBatchAddPicturesFromUrlFailed(action) {
    const text = yield select(state => state.displayTexts);
    const responseCode = action.payload.response.status;
    const reason = findReason(responseCode, text);
    const message = text.batchAddPicturesFromUrlFailed + ': ' + reason;
    yield put(SET_MODIFY_FAILED_ERROR(message));
}

function* createErrorMessageForSaveModifiedPictureFailed(action) {
    const text = yield select(state => state.displayTexts);
    const responseCode = action.payload.response.status;
    const reason = findReason(responseCode, text);
    const message = text.saveModifiedPictureFailed + ': ' + reason;
    yield put(SET_MODIFY_FAILED_ERROR(message));
}

function* createErrorMessageForSaveAddedPictureFailed(action) {
    const text = yield select(state => state.displayTexts);
    const responseCode = action.payload.response.status;
    const reason = findReason(responseCode, text);
    const message = text.saveAddedPictureFailed + ': ' + reason;
    yield put(SET_MODIFY_FAILED_ERROR(message));
}

function* createErrorMessageForDeleteFailed(action) {
    const text = yield select(state => state.displayTexts);
    const responseCode = action.payload.response.status;
    const reason = findReason(responseCode, text);
    const message = text.deleteFailed + ': ' + reason;
    yield put(SET_MODIFY_FAILED_ERROR(message));
}

function* createErrorMessageForSortFailed(action) {
    const text = yield select(state => state.displayTexts);
    const responseCode = action.payload.response.status;
    const reason = findReason(responseCode, text);
    const message = text.sortFailed + ': ' + reason;
    yield put(SET_MODIFY_FAILED_ERROR(message));
}

function* createErrorMessageForMoveAlbumEntryUpFailed(action) {
    const text = yield select(state => state.displayTexts);
    const responseCode = action.payload.response.status;
    const reason = findReason(responseCode, text);
    const message = text.moveAlbumEntryUpFailed + ': ' + reason;
    yield put(SET_MODIFY_FAILED_ERROR(message));
}

function* createErrorMessageForMoveAlbumEntryDownFailed(action) {
    const text = yield select(state => state.displayTexts);
    const responseCode = action.payload.response.status;
    const reason = findReason(responseCode, text);
    const message = text.moveAlbumEntryDownFailed + ': ' + reason;
    yield put(SET_MODIFY_FAILED_ERROR(message));
}

function* createErrorMessageForMoveAlbumEntryLeftFailed(action) {
    const text = yield select(state => state.displayTexts);
    const responseCode = action.payload.response.status;
    const reason = findReason(responseCode, text);
    const message = text.moveAlbumEntryLeftFailed + ': ' + reason;
    yield put(SET_MODIFY_FAILED_ERROR(message));
}

function* createErrorMessageForMoveAlbumEntryRightFailed(action) {
    const text = yield select(state => state.displayTexts);
    const responseCode = action.payload.response.status;
    const reason = findReason(responseCode, text);
    const message = text.moveAlbumEntryRightFailed + ': ' + reason;
    yield put(SET_MODIFY_FAILED_ERROR(message));
}

function findReason(responseCode, text) {
    if (responseCode === 401) {
        return text.notLoggedIn;
    }

    if (responseCode === 403) {
        return text.notAuthorizedPleaseLoginDifferentUser;
    }

    return text.unknownReason;
}
