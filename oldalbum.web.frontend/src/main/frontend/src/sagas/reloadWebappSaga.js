import { takeLatest, select } from 'redux-saga/effects';
import {
    TOGGLE_ALBUMENTRY_REQUIRE_LOGIN_FAILURE,
    LOGOUT_RECEIVE,
} from '../reduxactions';

export default function* reloadWebappSaga() {
    yield takeLatest(TOGGLE_ALBUMENTRY_REQUIRE_LOGIN_FAILURE, reloadWebapp);
    yield takeLatest(LOGOUT_RECEIVE, reloadWebapp);
}

function* reloadWebapp() {
    const currentLocation = yield select(state => state.router.location.pathname);
    location.href = currentLocation;
}
