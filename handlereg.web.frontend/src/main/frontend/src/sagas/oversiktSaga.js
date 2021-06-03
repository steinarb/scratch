import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    OVERSIKT_HENT,
    OVERSIKT_MOTTA,
    OVERSIKT_ERROR,
    HANDLINGER_HENT,
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

export default function* oversiktSaga() {
    yield takeLatest(OVERSIKT_HENT, mottaOversikt);
}
