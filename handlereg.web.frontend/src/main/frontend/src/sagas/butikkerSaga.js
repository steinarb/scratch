import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    BUTIKKER_HENT,
    BUTIKKER_MOTTA,
    BUTIKKER_ERROR,
} from '../actiontypes';

function hentButikker() {
    return axios.get('/handlereg/api/butikker');
}

function* mottaButikker(action) {
    try {
        const response = yield call(hentButikker);
        const butikker = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(BUTIKKER_MOTTA(butikker));
    } catch (error) {
        yield put(BUTIKKER_ERROR(error));
    }
}

export default function* butikkerSaga() {
    yield takeLatest(BUTIKKER_HENT, mottaButikker);
}
