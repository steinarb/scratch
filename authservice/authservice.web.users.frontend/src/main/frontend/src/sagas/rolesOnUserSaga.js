import { takeLatest, select, put } from 'redux-saga/effects';
import {
    USER_SELECTED,
    USERROLES_RECEIVED,
    ROLES_ON_USER_UPDATE,
    ROLES_NOT_ON_USER_UPDATE,
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

export default function* rolesOnUserSaga() {
    yield takeLatest(USERROLES_RECEIVED, findRolesOnUsersAndFindRolesNotOnUsers);
    yield takeLatest(USER_SELECTED, findRolesOnUsersAndFindRolesNotOnUsers);
}
