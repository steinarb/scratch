import { takeLatest, call, put, select } from 'redux-saga/effects';
import axios from 'axios';
import {
    BUTIKKER_HENT,
    BUTIKKER_MOTTA,
    BUTIKKER_ERROR,
    VELG_BUTIKK,
    VALGT_BUTIKK,
} from '../actiontypes';

function hentButikker() {
    return axios.get('/handlereg/api/butikker');
}

function* mottaButikker() {
    try {
        const response = yield call(hentButikker);
        const butikker = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(BUTIKKER_MOTTA(butikker));
    } catch (error) {
        yield put(BUTIKKER_ERROR(error));
    }
}

const uvalgtButikk = { storeId: -1, butikknavn: '', gruppe: 2 };

function* velgButikk(action) {
    const indeks = action.payload;
    const butikker = yield select(state => state.butikker);
    const butikk = butikker[indeks] || uvalgtButikk;
    yield put(VALGT_BUTIKK({ ...butikk }));
}

export default function* butikkerSaga() {
    yield takeLatest(BUTIKKER_HENT, mottaButikker);
    yield takeLatest(VELG_BUTIKK, velgButikk);
}
