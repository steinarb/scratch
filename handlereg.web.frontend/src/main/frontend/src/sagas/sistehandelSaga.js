import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    SISTEHANDEL_HENT,
    SISTEHANDEL_MOTTA,
    SISTEHANDEL_ERROR,
} from '../actiontypes';

export default function* sistehandelSaga() {
    yield takeLatest(SISTEHANDEL_HENT, mottaSistehandel);
}

function* mottaSistehandel() {
    try {
        const response = yield call(hentSistehandel);
        const sistehandel = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(SISTEHANDEL_MOTTA(sistehandel));
    } catch (error) {
        yield put(SISTEHANDEL_ERROR(error));
    }
}

function hentSistehandel() {
    return axios.get('/api/statistikk/sistehandel');
}
