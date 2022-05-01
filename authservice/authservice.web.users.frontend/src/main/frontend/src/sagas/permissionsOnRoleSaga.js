import { takeLatest, select, put } from 'redux-saga/effects';
import {
    SELECTED_ROLE,
    ROLEPERMISSIONS_RECEIVE,
    ADD_PERMISSON_TO_ROLE_RECEIVE,
    PERMISSIONS_ON_ROLE_UPDATE,
    PERMISSIONS_NOT_ON_ROLE_UPDATE,
    ADD_PERMISSION_BUTTON_CLICKED,
    ADD_PERMISSON_TO_ROLE_REQUEST,
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

function* addPermissionToRole() {
    const roleAndPermissions = yield select(state => ({
        role: { rolename: state.rolename },
        permissions: state.permissionsNotOnRole.filter(p => p.id === state.selectedInPermissionsNotOnRole),
    }));
    yield put(ADD_PERMISSON_TO_ROLE_REQUEST(roleAndPermissions));
}

export default function* permissionsOnRoleSaga() {
    yield takeLatest(ROLEPERMISSIONS_RECEIVE, findPermissionsOnRolesAndFindPermissionsNotOnRoles);
    yield takeLatest(ADD_PERMISSON_TO_ROLE_RECEIVE, findPermissionsOnRolesAndFindPermissionsNotOnRoles);
    yield takeLatest(SELECTED_ROLE, findPermissionsOnRolesAndFindPermissionsNotOnRoles);
    yield takeLatest(ADD_PERMISSION_BUTTON_CLICKED, addPermissionToRole);
}
