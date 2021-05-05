import { takeLatest, call, put, select } from 'redux-saga/effects';
import axios from 'axios';
import {
    OVERSIKT_MOTTA,
    FAVORITTER_HENT,
    FAVORITTER_MOTTA,
    FAVORITTER_ERROR,
} from '../actiontypes';

function hentFavoritter(username) {
    return axios.get('/handlereg/api/favoritter', { params: { username } });
}

function* mottaFavoritter() {
    try {
        const username = yield select(state => state.username);
        const response = yield call(hentFavoritter, username);
        const favoritter = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(FAVORITTER_MOTTA(favoritter));
    } catch (error) {
        yield put(FAVORITTER_ERROR(error));
    }
}

export default function* favoritterSaga() {
    yield takeLatest(OVERSIKT_MOTTA, mottaFavoritter);
    yield takeLatest(FAVORITTER_HENT, mottaFavoritter);
}
