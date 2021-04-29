import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    SUMYEAR_HENT,
    SUMYEAR_MOTTA,
    SUMYEAR_ERROR,
} from '../actiontypes';

function hentSumyear() {
    return axios.get('/handlereg/api/statistikk/sumyear');
}

function* mottaSumyear(action) {
    try {
        const response = yield call(hentSumyear);
        const sumyear = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(SUMYEAR_MOTTA(sumyear));
    } catch (error) {
        yield put(SUMYEAR_ERROR(error));
    }
}

export default function* sumyearSaga() {
    yield takeLatest(SUMYEAR_HENT, mottaSumyear);
}
