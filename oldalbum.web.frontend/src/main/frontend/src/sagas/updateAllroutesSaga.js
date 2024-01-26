import { takeLatest, put } from 'redux-saga/effects';
import {
    UPDATE_ALLROUTES,
    SAVE_MODIFIED_ALBUM_RECEIVE,
    SAVE_ADDED_ALBUM_RECEIVE,
    SAVE_MODIFIED_PICTURE_RECEIVE,
    SAVE_ADDED_PICTURE_RECEIVE,
    BATCH_ADD_PICTURES_FROM_URL_RECEIVE,
    SORT_ALBUM_ENTRIES_BY_DATE_RECEIVE,
    DELETE_ALBUMENTRY_RECEIVE,
    DELETE_SELECTION_RECEIVE,
    MOVE_ALBUMENTRY_UP_RECEIVE,
    MOVE_ALBUMENTRY_LEFT_RECEIVE,
    MOVE_ALBUMENTRY_DOWN_RECEIVE,
    MOVE_ALBUMENTRY_RIGHT_RECEIVE,
} from '../reduxactions';

export default function* updateAllroutesSaga() {
    yield takeLatest(SAVE_MODIFIED_ALBUM_RECEIVE, sendUpdateAllroutesAction);
    yield takeLatest(SAVE_ADDED_ALBUM_RECEIVE, sendUpdateAllroutesAction);
    yield takeLatest(SAVE_MODIFIED_PICTURE_RECEIVE, sendUpdateAllroutesAction);
    yield takeLatest(SAVE_ADDED_PICTURE_RECEIVE, sendUpdateAllroutesAction);
    yield takeLatest(BATCH_ADD_PICTURES_FROM_URL_RECEIVE, sendUpdateAllroutesAction);
    yield takeLatest(SORT_ALBUM_ENTRIES_BY_DATE_RECEIVE, sendUpdateAllroutesAction);
    yield takeLatest(DELETE_ALBUMENTRY_RECEIVE, sendUpdateAllroutesAction);
    yield takeLatest(DELETE_SELECTION_RECEIVE, sendUpdateAllroutesAction);
    yield takeLatest(MOVE_ALBUMENTRY_UP_RECEIVE, sendUpdateAllroutesAction);
    yield takeLatest(MOVE_ALBUMENTRY_LEFT_RECEIVE, sendUpdateAllroutesAction);
    yield takeLatest(MOVE_ALBUMENTRY_DOWN_RECEIVE, sendUpdateAllroutesAction);
    yield takeLatest(MOVE_ALBUMENTRY_RIGHT_RECEIVE, sendUpdateAllroutesAction);
}

function* sendUpdateAllroutesAction(action) {
    yield put(UPDATE_ALLROUTES(action.payload));
}
