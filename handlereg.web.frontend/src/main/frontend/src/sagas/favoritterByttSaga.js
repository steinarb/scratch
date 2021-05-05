import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    BYTT_FAVORITTER,
    FAVORITTER_MOTTA,
    BYTT_FAVORITTER_ERROR,
} from '../actiontypes';

function sendByttFavoritter(favorittpar) {
    return axios.post('/handlereg/api/favoritter/bytt', favorittpar);
}

function* byttFavoritter(action) {
    try {
        const response = yield call(sendByttFavoritter, action.payload);
        const favoritter = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(FAVORITTER_MOTTA(favoritter));
    } catch (error) {
        yield put(BYTT_FAVORITTER_ERROR(error));
    }
}

export default function* favoritterByttSaga() {
    yield takeLatest(BYTT_FAVORITTER, byttFavoritter);
}
