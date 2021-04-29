import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    BUTIKK_LAGRE,
    BUTIKK_LAGRET,
    NYBUTIKK_ERROR,
} from '../actiontypes';

function lagreButikk(butikk) {
    return axios.post('/handlereg/api/endrebutikk', butikk);
}

function* mottaLagreButikk(action) {
    try {
        const butikk = action.payload;
        const response = yield call(lagreButikk, butikk);
        const oversikt = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(BUTIKK_LAGRET(oversikt));
    } catch (error) {
        yield put(NYBUTIKK_ERROR(error));
    }
}

export default function* lagrebutikkSaga() {
    yield takeLatest(BUTIKK_LAGRE, mottaLagreButikk);
}
