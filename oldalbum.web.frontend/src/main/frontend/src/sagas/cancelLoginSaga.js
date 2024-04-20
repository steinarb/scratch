import { takeLatest, put } from 'redux-saga/effects';
import { push } from 'redux-first-history';
import {
    LOGIN_CANCEL_BUTTON_CLICKED,
    CLEAR_ORIGINAL_REQUEST_URL_REQUEST,
} from '../reduxactions';

export default function* reloadWebappSaga() {
    yield takeLatest(LOGIN_CANCEL_BUTTON_CLICKED, reloadWebapp);
}

function* reloadWebapp(action) {
    const returnpath = action.payload;
    yield put(CLEAR_ORIGINAL_REQUEST_URL_REQUEST());
    yield put(push(returnpath));
}
