import { takeLatest, call, put, select } from 'redux-saga/effects';
import axios from 'axios';
import {
    BUTIKK_LAGRE,
    BUTIKK_LAGRET,
    NYBUTIKK_ERROR,
} from '../actiontypes';

export default function* lagrebutikkSaga() {
    yield takeLatest(BUTIKK_LAGRE, mottaLagreButikk);
}

function* mottaLagreButikk(action) {
    try {
        const butikknavn = action.payload;
        const butikk = yield select(state => state.butikk);
        const response = yield call(lagreButikk, { ...butikk, butikknavn });
        const oversikt = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(BUTIKK_LAGRET(oversikt));
    } catch (error) {
        yield put(NYBUTIKK_ERROR(error));
    }
}

function lagreButikk(butikk) {
    return axios.post('/api/endrebutikk', butikk);
}
