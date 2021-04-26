import { takeLatest, call, put, select } from 'redux-saga/effects';
import axios from 'axios';
import {
    LEGG_TIL_FAVORITT,
    FAVORITTER_MOTTA,
    LEGG_TIL_FAVORITT_ERROR,
    VELG_FAVORITTBUTIKK,
} from '../actiontypes';

function sendLeggTilFavoritt(nyFavoritt) {
    return axios.post('/handlereg/api/favoritt/leggtil', nyFavoritt);
}

function* leggTilFavoritt() {
    try {
        const brukernavn = yield select(state => state.oversikt.brukernavn);
        const storeId = yield select(state => state.favorittbutikk);
        const nyFavoritt = { brukernavn, butikk: { storeId } };
        const response = yield call(sendLeggTilFavoritt, nyFavoritt);
        const favoritter = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(FAVORITTER_MOTTA(favoritter));
        yield put(VELG_FAVORITTBUTIKK(-1));
    } catch (error) {
        yield put(LEGG_TIL_FAVORITT_ERROR(error));
    }
}

export default function* favorittLeggTilSaga() {
    yield takeLatest(LEGG_TIL_FAVORITT, leggTilFavoritt);
}
