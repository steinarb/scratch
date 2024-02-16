import { takeLatest, select } from 'redux-saga/effects';
import { LOGOUT_MOTTA } from '../actiontypes';

export default function* reloadOnLogoutSaga() {
    yield takeLatest(LOGOUT_MOTTA, reloadPageInBrowserOnSuccessfulLogout);
}

function* reloadPageInBrowserOnSuccessfulLogout(action) {
    if (!action.payload.suksess) {
        const basename = yield select(state => state.basename);
        location.href = basename + '/';
    }
}
