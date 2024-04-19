import { takeLatest } from 'redux-saga/effects';
import { TOGGLE_ALBUMENTRY_REQUIRE_LOGIN_FAILURE } from '../reduxactions';

export default function* reloadWebappSaga() {
    yield takeLatest(TOGGLE_ALBUMENTRY_REQUIRE_LOGIN_FAILURE, reloadWebapp);
}

function reloadWebapp() {
    location.href.reload();
}
