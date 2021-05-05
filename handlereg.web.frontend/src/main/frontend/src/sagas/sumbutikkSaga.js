import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    SUMBUTIKK_HENT,
    SUMBUTIKK_MOTTA,
    SUMBUTIKK_ERROR,
} from '../actiontypes';

function hentSumbutikk() {
    return axios.get('/handlereg/api/statistikk/sumbutikk');
}

function* mottaSumbutikk() {
    try {
        const response = yield call(hentSumbutikk);
        const sumbutikk = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(SUMBUTIKK_MOTTA(sumbutikk));
    } catch (error) {
        yield put(SUMBUTIKK_ERROR(error));
    }
}

export default function* sumbutikkSaga() {
    yield takeLatest(SUMBUTIKK_HENT, mottaSumbutikk);
}
