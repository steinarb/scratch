import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    SLETT_FAVORITT,
    FAVORITTER_MOTTA,
    SLETT_FAVORITT_ERROR,
} from '../actiontypes';

function sendSlettFavoritt(favoritt) {
    return axios.post('/handlereg/api/favoritt/slett', favoritt);
}

function* slettFavoritt(action) {
    try {
        const response = yield call(sendSlettFavoritt, action.payload);
        const favoritter = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(FAVORITTER_MOTTA(favoritter));
    } catch (error) {
        yield put(SLETT_FAVORITT_ERROR(error));
    }
}

export default function* favorittSlettSaga() {
    yield takeLatest(SLETT_FAVORITT, slettFavoritt);
}
