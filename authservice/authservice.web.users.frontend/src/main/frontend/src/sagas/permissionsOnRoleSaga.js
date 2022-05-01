import { takeLatest, select, put } from 'redux-saga/effects';
import {
    SELECTED_ROLE,
    ROLEPERMISSIONS_RECEIVED,
    PERMISSIONS_ON_ROLE_UPDATE,
    PERMISSIONS_NOT_ON_ROLE_UPDATE,
} from '../actiontypes';

function* findPermissionsOnRolesAndFindPermissionsNotOnRoles() {
    const { permissionsOnRole, permissionsNotOnRole } = yield select(state => {
        const permissionsOnRole = state.rolepermissions[state.rolename] || [];
        return {
            permissionsOnRole,
            permissionsNotOnRole: state.permissions.filter(p => !permissionsOnRole.find(por => por.id === p.id)),
        };
    });
    yield put(PERMISSIONS_ON_ROLE_UPDATE(permissionsOnRole));
    yield put(PERMISSIONS_NOT_ON_ROLE_UPDATE(permissionsNotOnRole));
}

export default function* permissionsOnRoleSaga() {
    yield takeLatest(ROLEPERMISSIONS_RECEIVED, findPermissionsOnRolesAndFindPermissionsNotOnRoles);
    yield takeLatest(SELECTED_ROLE, findPermissionsOnRolesAndFindPermissionsNotOnRoles);
}
