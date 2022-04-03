import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    OVERSIKT_HENT,
    OVERSIKT_MOTTA,
    OVERSIKT_ERROR,
    HANDLINGER_HENT,
    LOGINTILSTAND_MOTTA,
} from '../actiontypes';

function hentOversikt() {
    return axios.get('/handlereg/api/oversikt');
}

function* mottaOversikt() {
    try {
        const response = yield call(hentOversikt);
        const oversikt = (response.headers['content-type'] === 'application/json') ? response.data : {};
        yield put(OVERSIKT_MOTTA(oversikt));
        const accountid = oversikt.accountid;
        yield put(HANDLINGER_HENT(accountid));
    } catch (error) {
        yield put(OVERSIKT_ERROR(error));
    }
}

function* hentOversiktDersomInnloggetOgAutorisert(action) {
    const { suksess, authorized } = action.payload;
    if (suksess && authorized) {
        yield put(OVERSIKT_HENT());
    }
}

export default function* oversiktSaga() {
    yield takeLatest(OVERSIKT_HENT, mottaOversikt);
    yield takeLatest(LOGINTILSTAND_MOTTA, hentOversiktDersomInnloggetOgAutorisert);
}
