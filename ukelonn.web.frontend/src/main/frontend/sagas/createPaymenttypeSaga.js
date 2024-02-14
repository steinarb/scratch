import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    CREATE_PAYMENTTYPE_REQUEST,
    CREATE_PAYMENTTYPE_RECEIVE,
    CREATE_PAYMENTTYPE_FAILURE,
} from '../actiontypes';

function doCreatePaymenttype(paymenttype) {
    return axios.post('/api/admin/paymenttype/create', paymenttype);
}

function* requestReceiveCreatePaymenttype(action) {
    try {
        const response = yield call(doCreatePaymenttype, action.payload);
        const paymenttypes = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(CREATE_PAYMENTTYPE_RECEIVE(paymenttypes));
    } catch (error) {
        yield put(CREATE_PAYMENTTYPE_FAILURE(error));
    }
}

export default function* createPaymenttypeSaga() {
    yield takeLatest(CREATE_PAYMENTTYPE_REQUEST, requestReceiveCreatePaymenttype);
}
