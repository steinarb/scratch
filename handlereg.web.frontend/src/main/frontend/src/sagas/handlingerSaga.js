import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    HANDLINGER_HENT,
    HANDLINGER_MOTTA,
    HANDLINGER_ERROR,
} from '../actiontypes';

export default function* handlingerSaga() {
    yield takeLatest(HANDLINGER_HENT, mottaHandlinger);
}

function* mottaHandlinger(action) {
    try {
        const accountid = action.payload;
        const response = yield call(hentHandlinger, accountid);
        const handlinger = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(HANDLINGER_MOTTA(handlinger));
    } catch (error) {
        yield put(HANDLINGER_ERROR(error));
    }
}

function hentHandlinger(accountid) {
    return axios.get('/api/handlinger/' + accountid);
}
