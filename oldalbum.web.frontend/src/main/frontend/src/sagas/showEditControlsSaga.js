import { takeLatest, select, put } from 'redux-saga/effects';
import {
    LOGIN_CHECK_RECEIVE,
    LOGOUT_RECEIVE,
    TOGGLE_EDIT_MODE_ON,
    TOGGLE_EDIT_MODE_OFF,
    SHOW_EDIT_CONTROLS,
    HIDE_EDIT_CONTROLS,
} from '../reduxactions';

export default function* showEditControlSaga() {
    yield takeLatest(LOGIN_CHECK_RECEIVE, modifyShowEditControlFlag);
    yield takeLatest(LOGOUT_RECEIVE, modifyShowEditControlFlag);
    yield takeLatest(TOGGLE_EDIT_MODE_ON, modifyShowEditControlFlag);
    yield takeLatest(TOGGLE_EDIT_MODE_OFF, modifyShowEditControlFlag);
}

function* modifyShowEditControlFlag() {
    const loggedIn = yield select(state => state.loggedIn);
    const editMode = yield select(state => state.editMode);
    const canModifyAlbum = yield select(state => state.canModifyAlbum);
    const showEditControl = loggedIn && editMode && canModifyAlbum;
    if (showEditControl) {
        yield put(SHOW_EDIT_CONTROLS());
    } else {
        yield put(HIDE_EDIT_CONTROLS());
    }
}
