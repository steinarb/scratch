import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    HANDLINGERBUTIKK_HENT,
    HANDLINGERBUTIKK_MOTTA,
    HANDLINGERBUTIKK_ERROR,
} from '../actiontypes';

function hentHandlingerbutikk() {
    return axios.get('/handlereg/api/statistikk/handlingerbutikk');
}

function* mottaHandlingerbutikk() {
    try {
        const response = yield call(hentHandlingerbutikk);
        const handlingerbutikk = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(HANDLINGERBUTIKK_MOTTA(handlingerbutikk));
    } catch (error) {
        yield put(HANDLINGERBUTIKK_ERROR(error));
    }
}

export default function* handlingerbutikkSaga() {
    yield takeLatest(HANDLINGERBUTIKK_HENT, mottaHandlingerbutikk);
}
