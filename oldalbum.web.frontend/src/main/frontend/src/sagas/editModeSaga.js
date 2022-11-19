import { takeLatest, select } from 'redux-saga/effects';
import Cookies from 'js-cookie';
import {
    TOGGLE_EDIT_MODE_ON,
    TOGGLE_EDIT_MODE_OFF,
} from '../reduxactions';

export default function* editModeSaga() {
    yield takeLatest(TOGGLE_EDIT_MODE_ON, updateEditModeCookie);
    yield takeLatest(TOGGLE_EDIT_MODE_OFF, updateEditModeCookie);
}

function* updateEditModeCookie() {
    const editMode = yield select(state => state.editMode);
    yield Cookies.set('editMode', editMode);
}
