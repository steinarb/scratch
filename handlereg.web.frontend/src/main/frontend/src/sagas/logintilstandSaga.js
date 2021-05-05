import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    LOGINTILSTAND_HENT,
    LOGINTILSTAND_MOTTA,
    LOGINTILSTAND_ERROR,
} from '../actiontypes';

function sendLogintilstand() {
    return axios.get('/handlereg/api/logintilstand');
}

function* mottaLogintilstandResultat() {
    try {
        const response = yield call(sendLogintilstand);
        const logoutresult = (response.headers['content-type'] === 'application/json') ? response.data : {};
        yield put(LOGINTILSTAND_MOTTA(logoutresult));
    } catch (error) {
        yield put(LOGINTILSTAND_ERROR(error));
    }
}

export default function* logintilstandSaga() {
    yield takeLatest(LOGINTILSTAND_HENT, mottaLogintilstandResultat);
}
