import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    NYBUTIKK_REGISTRER,
    NYBUTIKK_LAGRET,
    NYBUTIKK_ERROR,
} from '../actiontypes';

function registrerNybutikk(butikk) {
    return axios.post('/handlereg/api/nybutikk', butikk);
}

function* mottaNybutikk(action) {
    try {
        const butikk = action.payload;
        const response = yield call(registrerNybutikk, butikk);
        const oversikt = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(NYBUTIKK_LAGRET(oversikt));
    } catch (error) {
        yield put(NYBUTIKK_ERROR(error));
    }
}

export default function* nybutikkSaga() {
    yield takeLatest(NYBUTIKK_REGISTRER, mottaNybutikk);
}
