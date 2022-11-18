import { takeLatest, select } from 'redux-saga/effects';
import Cookies from 'js-cookie';
import {
    TOGGLE_EDIT_MODE_ON,
    TOGGLE_EDIT_MODE_OFF,
} from '../reduxactions';

export default function* localeSaga() {
    yield takeLatest(TOGGLE_EDIT_MODE_ON, updateLocaleCookie);
    yield takeLatest(TOGGLE_EDIT_MODE_OFF, updateLocaleCookie);
}

function* updateLocaleCookie() {
    const editMode = yield select(state => state.editMode);
    yield Cookies.set('editMode', editMode);
}
