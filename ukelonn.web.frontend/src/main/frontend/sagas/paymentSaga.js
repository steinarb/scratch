import { takeLatest, put, select } from 'redux-saga/effects';
import {
    SELECT_PAYMENT_TYPE,
    MODIFY_PAYMENT_AMOUNT,
} from '../actiontypes';

function* selectPaymentType(action) {
    const transactionTypeId = parseInt(action.payload);
    if (transactionTypeId === -1) {
        const balance = yield select(state => state.accountBalance);
        yield put(MODIFY_PAYMENT_AMOUNT(balance));
    }
    const paymenttypes = yield select(state => state.paymenttypes);
    const paymenttype = paymenttypes.find(p => p.id === transactionTypeId);
    if (paymenttype && paymenttype.transactionAmount > 0) {
        yield put(MODIFY_PAYMENT_AMOUNT(paymenttype.transactionAmount));
    } else {
        const balance = yield select(state => state.accountBalance);
        yield put(MODIFY_PAYMENT_AMOUNT(balance));
    }
}

export default function* paymentSaga() {
    yield takeLatest(SELECT_PAYMENT_TYPE, selectPaymentType);
}
