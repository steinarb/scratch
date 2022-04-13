import { takeLatest, call, put, select } from 'redux-saga/effects';
import axios from 'axios';
import {
    MODIFY_USER_REQUEST,
    MODIFY_USER_RECEIVE,
    MODIFY_USER_FAILURE,
    SAVE_USER_BUTTON_CLICKED,
    CHANGE_ADMIN_STATUS,
} from '../actiontypes';


function doModifyUser(user) {
    return axios.post('/ukelonn/api/admin/user/modify', user);
}

function* requestReceiveModifyUserSaga(action) {
    try {
        const response = yield call(doModifyUser, action.payload);
        const users = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(MODIFY_USER_RECEIVE(users));
    } catch (error) {
        yield put(MODIFY_USER_FAILURE(error));
    }
}

function* collectAndSaveModifiedUser() {
    const userid = yield select(state => state.userid);
    const username = yield select(state => state.username);
    const email = yield select(state => state.email);
    const firstname = yield select(state => state.firstname);
    const lastname = yield select(state => state.lastname);
    const user = {
        userid,
        username,
        email,
        firstname,
        lastname,
    };
    yield put(MODIFY_USER_REQUEST(user));
    const administrator = yield select(state => state.userIsAdministrator);
    yield put(CHANGE_ADMIN_STATUS({ user, administrator }));
}

export default function* modifyUserSaga() {
    yield takeLatest(MODIFY_USER_REQUEST, requestReceiveModifyUserSaga);
    yield takeLatest(SAVE_USER_BUTTON_CLICKED, collectAndSaveModifiedUser);
}
