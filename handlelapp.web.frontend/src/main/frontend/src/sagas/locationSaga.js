import { takeLatest, select } from 'redux-saga/effects';
import { LOCATION_CHANGE } from 'connected-react-router';

function* locationChange(action) {
    const { location = {} } = action.payload || {};
    const { pathname = '' } = location;

    if (pathname === '/handlelapp/') {
        yield select(state => state.username);
    }
}

export default function* locationSaga() {
    yield takeLatest(LOCATION_CHANGE, locationChange);
}
