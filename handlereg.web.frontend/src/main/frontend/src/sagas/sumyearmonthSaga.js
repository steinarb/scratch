import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    SUMYEARMONTH_HENT,
    SUMYEARMONTH_MOTTA,
    SUMYEARMONTH_ERROR,
} from '../actiontypes';

export default function* sumyearmonthSaga() {
    yield takeLatest(SUMYEARMONTH_HENT, mottaSumyearmonth);
}

function* mottaSumyearmonth() {
    try {
        const response = yield call(hentSumyearmonth);
        const sumyearmonth = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(SUMYEARMONTH_MOTTA(sumyearmonth));
    } catch (error) {
        yield put(SUMYEARMONTH_ERROR(error));
    }
}

function hentSumyearmonth() {
    return axios.get('/api/statistikk/sumyearmonth');
}
