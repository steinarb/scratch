import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    TOGGLE_ALBUMENTRY_REQUIRE_LOGIN_REQUEST,
    TOGGLE_ALBUMENTRY_REQUIRE_LOGIN_RECEIVE,
    TOGGLE_ALBUMENTRY_REQUIRE_LOGIN_FAILURE,
} from '../reduxactions';

export default function* togglepasswordprotectionSaga() {
    yield takeLatest(TOGGLE_ALBUMENTRY_REQUIRE_LOGIN_REQUEST, receiveRoutes);
}

function* receiveRoutes(action) {
    try {
        const response = yield call(getTogglepasswordprotection, action.payload);
        const routes = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(TOGGLE_ALBUMENTRY_REQUIRE_LOGIN_RECEIVE(routes));
    } catch (error) {
        yield put(TOGGLE_ALBUMENTRY_REQUIRE_LOGIN_FAILURE(error));
    }
}

function getTogglepasswordprotection(id) {
    return axios.get('/api/togglepasswordprotection/' + id.toString());
}
