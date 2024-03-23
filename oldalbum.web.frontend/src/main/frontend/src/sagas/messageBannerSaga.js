import { takeLatest, select, put, delay } from 'redux-saga/effects';
import {
    SUCCESSFULL_CHANGE_OF_PASSWORD_REQUIREMENT,
    SET_MESSAGE_BANNER,
    CLEAR_MESSAGE_BANNER,
} from '../reduxactions';

export default function* messageBannerSaga() {
    yield takeLatest(SUCCESSFULL_CHANGE_OF_PASSWORD_REQUIREMENT, displaySuccessfullPasswordRequirementMessage);
}

export function* displaySuccessfullPasswordRequirementMessage(action) {
    const albumEntryId = action.payload;
    const albumEntry = yield select(state => state.albumentries[albumEntryId]);
    const text = yield select(state => state.displayTexts);
    const messageText = formatMessageText(albumEntry, text);
    yield put(SET_MESSAGE_BANNER(messageText));
    yield delay(5000); // 5s wait before taking down the banner
    yield put(CLEAR_MESSAGE_BANNER());
}

function formatMessageText(albumEntry, text) {
    const requireLogin = albumEntry.requireLogin;
    const entryType = albumEntry.album ? text.album : text.picture;
    const path = albumEntry.path;
    if (requireLogin) {
        return text.successfullyaddedpasswordprotection + ' ' + entryType + ' \'' + path + '\'';
    } else {
        return text.successfullyremovedpasswordprotection + ' ' + entryType + ' \'' + path + '\'';
    }
}
