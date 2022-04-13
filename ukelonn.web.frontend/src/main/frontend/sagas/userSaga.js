import { takeLatest, put, select } from 'redux-saga/effects';
import axios from 'axios';
import {
    SELECT_USER,
    MODIFY_USER_USERNAME,
    MODIFY_USER_EMAIL,
    MODIFY_USER_FIRSTNAME,
    MODIFY_USER_LASTNAME,
    MODIFY_USER_IS_ADMINISTRATOR,
    REQUEST_ADMIN_STATUS,
    USERS_RECEIVE,
    MODIFY_USER_RECEIVE,
    CREATE_USER_RECEIVE,
    CLEAR_USER,
} from '../actiontypes';

function* selectUser(action) {
    console.log('selectUser(1)');
    const userid = action.payload;
    console.log('selectUser(2)');
    if (userid === -1) {
        console.log('selectUser(3)');
        yield put(CLEAR_USER());
    } else {
        console.log('selectUser(4)');
        const users = yield select(state => state.users);
        console.log('selectUser(5)');
        const user = users.find(u => u.userid = userid);
        console.log('selectUser(6)');
        if (user) {
            console.log('selectUser(7)');
            const { username } = user;
            if (username) {
                console.log('selectUser(8)');
                yield put(MODIFY_USER_USERNAME(username));
                yield put(REQUEST_ADMIN_STATUS(username));
            }
            console.log('selectUser(9)');
            yield put(MODIFY_USER_EMAIL(user.email));
            yield put(MODIFY_USER_FIRSTNAME(user.firstname));
            yield put(MODIFY_USER_LASTNAME(user.lastname));
        }
    }
}

function* clearUserForm() {
    yield put(CLEAR_USER());
}

export default function* userSaga() {
    yield takeLatest(SELECT_USER, selectUser);
    yield takeLatest(USERS_RECEIVE, clearUserForm);
    yield takeLatest(MODIFY_USER_RECEIVE, clearUserForm);
    yield takeLatest(CREATE_USER_RECEIVE, clearUserForm);
}
