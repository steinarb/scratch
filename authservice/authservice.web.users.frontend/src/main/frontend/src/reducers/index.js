import { combineReducers } from 'redux';
import userid from './useridReducer';
import username from './usernameReducer';
import email from './emailReducer';
import firstname from './firstnameReducer';
import lastname from './lastnameReducer';
import users from './usersReducer';
import user from './userReducer';
import userroles from './userrolesReducer';
import rolesNotOnUser from './rolesNotOnUserReducer';
import selectedInRolesNotOnUser from './selectedInRolesNotOnUserReducer';
import rolesOnUser from './rolesOnUserReducer';
import selectedInRolesOnUser from './selectedInRolesOnUserReducer';
import password1 from './password1Reducer';
import password2 from './password2Reducer';
import passwordsNotIdentical from './passwordsNotIdenticalReducer';
import roles from './rolesReducer';
import role from './roleReducer';
import rolepermissions from './rolepermissionsReducer';
import permissionsNotOnRole from './permissionsNotOnRoleReducer';
import selectedInPermissionsNotOnRole from './selectedInPermissionsNotOnRoleReducer';
import permissionsOnRole from './permissionsOnRoleReducer';
import selectedInPermissionsOnRole from './selectedInPermissionsOnRoleReducer';
import permissions from './permissionsReducer';
import permission from './permissionReducer';
import formfield from './formfieldReducer';
import errors from './errorsReducer';

const rootsReducer = combineReducers({
    userid,
    username,
    email,
    firstname,
    lastname,
    users,
    user,
    userroles,
    rolesNotOnUser,
    selectedInRolesNotOnUser,
    rolesOnUser,
    selectedInRolesOnUser,
    password1,
    password2,
    passwordsNotIdentical,
    roles,
    role,
    rolepermissions,
    permissionsNotOnRole,
    selectedInPermissionsNotOnRole,
    permissionsOnRole,
    selectedInPermissionsOnRole,
    permissions,
    permission,
    formfield,
    errors,
});

export default rootsReducer;
