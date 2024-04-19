import { takeLatest, select } from 'redux-saga/effects';
import { TOGGLE_ALBUMENTRY_REQUIRE_LOGIN_FAILURE } from '../reduxactions';

export default function* reloadWebappSaga() {
    yield takeLatest(TOGGLE_ALBUMENTRY_REQUIRE_LOGIN_FAILURE, reloadWebapp);
}

function* reloadWebapp() {
    const currentLocation = yield select(state => state.location.basename);
    location.href = currentLocation;
}
