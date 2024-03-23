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
    const messageText = formatMessageText(albumEntry);
    yield put(SET_MESSAGE_BANNER(messageText));
    yield delay(5000); // 5s wait before taking down the banner
    yield put(CLEAR_MESSAGE_BANNER());
}

function formatMessageText(albumEntry) {
    const requireLogin = albumEntry.requireLogin;
    const isAlbum = albumEntry.album;
    const path = albumEntry.path;
    if (requireLogin) {
        if (isAlbum) {
            return 'Successfully password protected album \'' + path + '\'';
        } else {
            return 'Successfully password protected picture \'' + path + '\'';
        }
    } else {
        if (isAlbum) {
            return 'Successfully removed password protection for album \'' + path + '\'';
        } else {
            return 'Successfully removed password protection for picture \'' + path + '\'';
        }
    }
}
