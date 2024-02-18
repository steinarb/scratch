import { takeLatest, call, put, select } from 'redux-saga/effects';
import axios from 'axios';
import {
    LOGINTILSTAND_MOTTA,
    FAVORITTER_HENT,
    FAVORITTER_MOTTA,
    FAVORITTER_ERROR,
} from '../actiontypes';

function hentFavoritter(username) {
    return axios.get('/api/favoritter', { params: { username } });
}

function* sendFavoritterHent() {
    const loginresultat = yield select (state => state.loginresultat);
    const authorized = loginresultat.authorized;
    const brukernavn = loginresultat.brukernavn;
    if (authorized && brukernavn) {
        yield put(FAVORITTER_HENT());
    }
}

function* mottaFavoritter() {
    try {
        const username = yield select(state => state.loginresultat.brukernavn);
        const response = yield call(hentFavoritter, username);
        const favoritter = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(FAVORITTER_MOTTA(favoritter));
    } catch (error) {
        yield put(FAVORITTER_ERROR(error));
    }
}

export default function* favoritterSaga() {
    yield takeLatest(LOGINTILSTAND_MOTTA, sendFavoritterHent);
    yield takeLatest(FAVORITTER_HENT, mottaFavoritter);
}
