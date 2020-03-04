import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import oversikt, { OVERSIKT_HENT } from '../reducers/oversikt';
import { HANDLINGER_HENT } from '../reducers/handlinger';
import nyhandling from '../reducers/nyhandling';
const { NYHANDLING_LAGRET } = nyhandling.actions;
const { OVERSIKT_MOTTA } = oversikt.actions;
import errors from '../reducers/errors';
const { OVERSIKT_ERROR } = errors.actions;

function hentOversikt() {
    return axios.get('/handlereg/api/oversikt');
}

function* mottaOversikt(action) {
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
    yield takeLatest(NYHANDLING_LAGRET, mottaOversikt);
}
