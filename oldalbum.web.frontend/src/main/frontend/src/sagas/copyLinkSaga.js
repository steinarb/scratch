import { takeLatest, select, put, delay } from 'redux-saga/effects';
import {
    SHARE_LINK,
    OPEN_WARNING_DIALOG_ENTRY_IS_PASSWORD_PROTECTED,
    SET_MESSAGE_BANNER,
    CLEAR_MESSAGE_BANNER,
    CLOSE_WARNING_DIALOG_ENTRY_IS_PASSWORD_PROTECTED,
    REMOVE_PASSWORD_PROTECTION_AND_CLOSE_WARNING_DIALOG,
    TOGGLE_ALBUMENTRY_REQUIRE_LOGIN_REQUEST,
    CLEAR_SHARED_LINK_ITEM,
} from '../reduxactions';

export default function* copyLinkSaga() {
    yield takeLatest(SHARE_LINK, copyLinkButton);
    yield takeLatest(REMOVE_PASSWORD_PROTECTION_AND_CLOSE_WARNING_DIALOG, removePasswordProtectionAndCloseWarningDialog);
}

function* copyLinkButton(action) {
    const item = action.payload;
    if (item.requireLogin) {
        yield put(OPEN_WARNING_DIALOG_ENTRY_IS_PASSWORD_PROTECTED());
    }

    copyCurrentUrlToClipboard();
    const text = yield select(state => state.displayTexts);
    yield put(SET_MESSAGE_BANNER(text.urlcopiedtoclipboard));
    yield delay(2000); // 2s wait before taking down the banner
    yield put(CLEAR_MESSAGE_BANNER());
}

function copyCurrentUrlToClipboard() {
    const currentLocation = document.createElement('textarea');
    currentLocation.value = window.location.href;
    document.body.appendChild(currentLocation);
    currentLocation.select();
    document.execCommand("copy");
    document.body.removeChild(currentLocation);
}

function* removePasswordProtectionAndCloseWarningDialog() {
    const sharedItem = yield select(state => state.sharedLinkItem);
    yield put(TOGGLE_ALBUMENTRY_REQUIRE_LOGIN_REQUEST(sharedItem.id));
    yield put(CLOSE_WARNING_DIALOG_ENTRY_IS_PASSWORD_PROTECTED());
    yield put(CLEAR_SHARED_LINK_ITEM());
}
