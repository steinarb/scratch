import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    USER_REMOVE_ROLE_REQUEST,
    USER_REMOVE_ROLE_RECEIVE,
    USERROLES_RECEIVED,
} from '../actiontypes';

function postUserRemoveRoles(userAndRoles) {
    return axios.post('/authservice/useradmin/api/user/removeroles', userAndRoles);
}

function* userRemoveRoles(action) {
    try {
        const response = yield call(postUserRemoveRoles, action.payload);
        const userroles = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(USER_REMOVE_ROLE_RECEIVE(userroles));
    } catch (error) {
        yield put(USERROLES_RECEIVED(error));
    }
}

export default function* userRemoveRolesSaga() {
    yield takeLatest(USER_REMOVE_ROLE_REQUEST, userRemoveRoles);
}
