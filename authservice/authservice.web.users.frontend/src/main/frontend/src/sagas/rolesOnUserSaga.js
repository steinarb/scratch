import { takeLatest, select, put } from 'redux-saga/effects';
import {
    SELECTED_USER,
    USERROLES_RECEIVED,
    USER_ADD_ROLE_RECEIVE,
    ROLES_ON_USER_UPDATE,
    ROLES_NOT_ON_USER_UPDATE,
    ADD_USER_ROLE_BUTTON_CLICKED,
    USER_ADD_ROLE_REQUEST,
    //REMOVE_USER_ROLE_BUTTON_CLICKED,
} from '../actiontypes';
import { emptyRole } from '../constants';

function* findRolesOnUsersAndFindRolesNotOnUsers() {
    const username = yield select(state => state.username);
    const allRoles = yield select(state => state.roles);
    const roles = allRoles.filter(r => r.id !== emptyRole.id);
    const userroles = yield select(state => state.userroles);
    const rolesOnUser = userroles[username] || [];
    yield put(ROLES_ON_USER_UPDATE(rolesOnUser));
    const rolesNotOnUser = roles.filter(r => !rolesOnUser.find(rou => rou.id === r.id));
    yield put(ROLES_NOT_ON_USER_UPDATE(rolesNotOnUser));
}

function* addRoleToUser() {
    const userAndRoles = yield select(state => ({
        user: {
            username: state.username,
        },
        roles: state.roles.filter(r => r.id === state.selectedInRolesNotOnUser),
    }));
    yield put(USER_ADD_ROLE_REQUEST(userAndRoles));
}

export default function* rolesOnUserSaga() {
    yield takeLatest(USERROLES_RECEIVED, findRolesOnUsersAndFindRolesNotOnUsers);
    yield takeLatest(USER_ADD_ROLE_RECEIVE, findRolesOnUsersAndFindRolesNotOnUsers);
    yield takeLatest(SELECTED_USER, findRolesOnUsersAndFindRolesNotOnUsers);
    yield takeLatest(ADD_USER_ROLE_BUTTON_CLICKED, addRoleToUser);
}
