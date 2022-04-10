import { takeLatest, call, put, select } from 'redux-saga/effects';
import axios from 'axios';
import {
    SELECT_ACCOUNT,
    MODIFY_ACCOUNT,
    MODIFY_ACCOUNT_ID,
    MODIFY_ACCOUNT_USERNAME,
    MODIFY_ACCOUNT_FIRSTNAME,
    MODIFY_ACCOUNT_LASTNAME,
    MODIFY_ACCOUNT_BALANCE,
    MODIFY_ACCOUNT_FULLNAME,
    UPDATE_ACCOUNT,
    ACCOUNT_REQUEST,
    ACCOUNT_RECEIVE,
    ACCOUNT_FAILURE,
    EARNINGS_SUM_OVER_YEAR_REQUEST,
    EARNINGS_SUM_OVER_YEAR_RECEIVE,
    EARNINGS_SUM_OVER_MONTH_REQUEST,
    EARNINGS_SUM_OVER_MONTH_RECEIVE,
    RECEIVED_NOTIFICATION,
    RECENTPAYMENTS_REQUEST,
    RECENTJOBS_REQUEST,
} from '../actiontypes';
import { emptyAccount } from '../constants';
import { findUsername } from '../common/login';


function doAccount(username) {
    return axios.get('/ukelonn/api/account/' + username );
}

// worker saga
function* receiveAccountSaga(action) {
    if (action.payload) {
        try {
            const response = yield call(doAccount, action.payload);
            const account = (response.headers['content-type'] === 'application/json') ? response.data : emptyAccount;
            yield put(ACCOUNT_RECEIVE(account));
            const username = account.username;
            yield put(EARNINGS_SUM_OVER_YEAR_REQUEST(username));
            yield put(EARNINGS_SUM_OVER_MONTH_REQUEST(username));
        } catch (error) {
            yield put(ACCOUNT_FAILURE(error));
        }
    } else {
        yield put(ACCOUNT_RECEIVE(emptyAccount));
        yield put(EARNINGS_SUM_OVER_YEAR_RECEIVE([]));
        yield put(EARNINGS_SUM_OVER_MONTH_RECEIVE([]));
    }
}

function* selectAccountSaga(action) {
    const accountId = action.payload;
    const accounts = yield select(state => state.accounts);
    const account = accounts.find(a => a.accountId === accountId);
    if (account) {
        yield put(MODIFY_ACCOUNT(account));
        yield put(MODIFY_ACCOUNT_ID(account.accountId));
        const { accountId, username } = account;
        yield put(MODIFY_ACCOUNT_USERNAME(username));
        yield put(MODIFY_ACCOUNT_FIRSTNAME(account.firstName));
        yield put(MODIFY_ACCOUNT_LASTNAME(account.lastName));
        yield put(MODIFY_ACCOUNT_BALANCE(account.balance));
        yield put(MODIFY_ACCOUNT_FULLNAME(account.fullName));
        if (username) {
            yield put(EARNINGS_SUM_OVER_YEAR_REQUEST(username));
            yield put(EARNINGS_SUM_OVER_MONTH_REQUEST(username));
        }
        if (accountId) {
            yield put(RECENTPAYMENTS_REQUEST(accountId));
            yield put(RECENTJOBS_REQUEST(accountId));
        }
    }
}

function* updateAccountSaga(action) {
    const payload = action.payload || {};
    const { username } = payload;
    yield put(EARNINGS_SUM_OVER_YEAR_REQUEST(username));
    yield put(EARNINGS_SUM_OVER_MONTH_REQUEST(username));
}

function* updateAccountOnNotification() {
    const username = yield select(findUsername);
    yield put(ACCOUNT_REQUEST(username));
}

export default function* accountSaga() {
    yield takeLatest(SELECT_ACCOUNT, selectAccountSaga);
    yield takeLatest(UPDATE_ACCOUNT, updateAccountSaga);
    yield takeLatest(ACCOUNT_REQUEST, receiveAccountSaga);
    yield takeLatest(RECEIVED_NOTIFICATION, updateAccountOnNotification);
}
