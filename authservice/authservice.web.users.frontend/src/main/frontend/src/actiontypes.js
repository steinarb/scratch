import { createAction } from '@reduxjs/toolkit';

export const USERS_REQUEST = createAction('USERS_REQUEST');
export const USERS_RECEIVE = createAction('USERS_RECEIVE');
export const USERS_FAILURE = createAction('USERS_FAILURE');
export const SELECT_USER = createAction('SELECT_USER');
export const SELECTED_USER = createAction('SELECTED_USER');
export const USERNAME_FIELD_MODIFIED = createAction('USERNAME_FIELD_MODIFIED');
export const EMAIL_FIELD_MODIFIED = createAction('EMAIL_FIELD_MODIFIED');
export const FIRSTNAME_FIELD_MODIFIED = createAction('FIRSTNAME_FIELD_MODIFIED');
export const LASTNAME_FIELD_MODIFIED = createAction('LASTNAME_FIELD_MODIFIED');
export const MODIFY_USER_BUTTON_CLICKED = createAction('MODIFY_USER_BUTTON_CLICKED');
export const SAVE_MODIFIED_USER_REQUEST = createAction('SAVE_MODIFIED_USER_REQUEST');
export const SAVE_MODIFIED_USER_RECEIVE = createAction('SAVE_MODIFIED_USER_RECEIVE');
export const SAVE_MODIFIED_USER_FAILURE = createAction('SAVE_MODIFIED_USER_FAILURE');
export const PASSWORD1_FIELD_MODIFIED = createAction('PASSWORD1_FIELD_MODIFIED');
export const PASSWORD2_FIELD_MODIFIED = createAction('PASSWORD2_FIELD_MODIFIED');
export const MODIFY_PASSWORDS_NOT_IDENTICAL = createAction('MODIFY_PASSWORDS_NOT_IDENTICAL');
export const CHANGE_PASSWORD_BUTTON_CLICKED = createAction('CHANGE_PASSWORD_BUTTON_CLICKED');
export const SAVE_PASSWORDS_MODIFY_REQUEST = createAction('SAVE_PASSWORDS_MODIFY_REQUEST');
export const SAVE_PASSWORDS_MODIFY_RECEIVE = createAction('SAVE_PASSWORDS_MODIFY_RECEIVE');
export const SAVE_PASSWORDS_MODIFY_FAILURE = createAction('SAVE_PASSWORDS_MODIFY_FAILURE');
export const ADD_USER_BUTTON_CLICKED = createAction('ADD_USER_BUTTON_CLICKED');
export const SAVE_ADDED_USER_REQUEST = createAction('SAVE_ADDED_USER_REQUEST');
export const SAVE_ADDED_USER_RECEIVE = createAction('SAVE_ADDED_USER_RECEIVE');
export const SAVE_ADDED_USER_FAILURE = createAction('SAVE_ADDED_USER_FAILURE');
export const ADD_USER_ROLE_BUTTON_CLICKED = createAction('ADD_USER_ROLE_BUTTON_CLICKED');
export const USER_ADD_ROLE_REQUEST = createAction('USER_ADD_ROLE_REQUEST');
export const USER_ADD_ROLE_RECEIVE = createAction('USER_ADD_ROLE_RECEIVE');
export const USER_ADD_ROLE_FAILURE = createAction('USER_ADD_ROLE_FAILURE');
export const REMOVE_USER_ROLE_BUTTON_CLICKED = createAction('REMOVE_USER_ROLE_BUTTON_CLICKED');
export const USER_REMOVE_ROLE_REQUEST = createAction('USER_REMOVE_ROLE_REQUEST');
export const USER_REMOVE_ROLE_RECEIVE = createAction('USER_REMOVE_ROLE_RECEIVE');
export const USER_REMOVE_ROLE_FAILURE = createAction('USER_REMOVE_ROLE_FAILURE');
export const USER_CLEAR = createAction('USER_CLEAR');
export const USERROLES_REQUEST = createAction('USERROLES_REQUEST');
export const USERROLES_RECEIVED = createAction('USERROLES_RECEIVED');
export const USERROLES_ERROR = createAction('USERROLES_ERROR');
export const SET_ROLES_NOT_ON_USER = createAction('SET_ROLES_NOT_ON_USER');
export const SELECT_ROLES_NOT_ON_USER = createAction('SELECT_ROLES_NOT_ON_USER');
export const ROLES_NOT_ON_USER_CLEAR = createAction('ROLES_NOT_ON_USER_CLEAR');
export const SET_ROLES_ON_USER = createAction('SET_ROLES_ON_USER');
export const SELECT_ROLES_ON_USER = createAction('SELECT_ROLES_ON_USER');
export const ROLES_ON_USER_CLEAR = createAction('ROLES_ON_USER_CLEAR');
export const PASSWORDS_CLEAR = createAction('PASSWORDS_CLEAR');
export const ROLES_REQUEST = createAction('ROLES_REQUEST');
export const ROLES_RECEIVED = createAction('ROLES_RECEIVED');
export const ROLES_ERROR = createAction('ROLES_ERROR');
export const SELECT_ROLE = createAction('SELECT_ROLE');
export const SELECTED_ROLE = createAction('SELECTED_ROLE');
export const ROLENAME_FIELD_MODIFIED = createAction('ROLENAME_FIELD_MODIFIED');
export const ROLE_DESCRIPTION_FIELD_MODIFIED = createAction('ROLE_DESCRIPTION_FIELD_MODIFIED');
export const MODIFY_ROLE_BUTTON_CLICKED = createAction('MODIFY_ROLE_BUTTON_CLICKED');
export const SAVE_MODIFIED_ROLE_REQUEST = createAction('SAVE_MODIFIED_ROLE_REQUEST');
export const SAVE_MODIFIED_ROLE_RECEIVE = createAction('SAVE_MODIFIED_ROLE_RECEIVE');
export const SAVE_MODIFIED_ROLE_FAILURE = createAction('SAVE_MODIFIED_ROLE_FAILURE');
export const ADD_ROLE_BUTTON_CLICKED = createAction('ADD_ROLE_BUTTON_CLICKED');
export const SAVE_ADDED_ROLE_REQUEST = createAction('SAVE_ADDED_ROLE_REQUEST');
export const SAVE_ADDED_ROLE_RECEIVE = createAction('SAVE_ADDED_ROLE_RECEIVE');
export const SAVE_ADDED_ROLE_FAILURE = createAction('SAVE_ADDED_ROLE_FAILURE');
export const ROLE_UPDATE = createAction('ROLE_UPDATE');
export const ROLE_CLEAR = createAction('ROLE_CLEAR');
export const ROLE_MODIFY = createAction('ROLE_MODIFY');
export const ROLE_ADD = createAction('ROLE_ADD');
export const ROLE_ADD_PERMISSIONS = createAction('ROLE_ADD_PERMISSIONS');
export const ROLE_REMOVE_PERMISSIONS = createAction('ROLE_REMOVE_PERMISSIONS');
export const ROLEPERMISSIONS_REQUEST = createAction('ROLEPERMISSIONS_REQUEST');
export const ROLEPERMISSIONS_RECEIVE = createAction('ROLEPERMISSIONS_RECEIVE');
export const ROLEPERMISSIONS_FAILURE = createAction('ROLEPERMISSIONS_FAILURE');
export const SET_PERMISSIONS_NOT_ON_ROLE = createAction('SET_PERMISSIONS_NOT_ON_ROLE');
export const PERMISSIONS_NOT_ON_ROLE_SELECT = createAction('PERMISSIONS_NOT_ON_ROLE_SELECT');
export const PERMISSIONS_NOT_ON_ROLE_CLEAR = createAction('PERMISSIONS_NOT_ON_ROLE_CLEAR');
export const SET_PERMISSIONS_ON_ROLE = createAction('SET_PERMISSIONS_ON_ROLE');
export const PERMISSIONS_ON_ROLE_SELECT = createAction('PERMISSIONS_ON_ROLE_SELECT');
export const PERMISSIONS_ON_ROLE_CLEAR = createAction('PERMISSIONS_ON_ROLE_CLEAR');
export const ADD_PERMISSION_BUTTON_CLICKED = createAction('ADD_PERMISSION_BUTTON_CLICKED');
export const ADD_PERMISSON_TO_ROLE_REQUEST = createAction('ADD_PERMISSON_TO_ROLE_REQUEST');
export const ADD_PERMISSON_TO_ROLE_RECEIVE = createAction('ADD_PERMISSON_TO_ROLE_RECEIVE');
export const ADD_PERMISSON_TO_ROLE_FAILURE = createAction('ADD_PERMISSON_TO_ROLE_FAILURE');
export const REMOVE_PERMISSION_BUTTON_CLICKED = createAction('REMOVE_PERMISSION_BUTTON_CLICKED');
export const REMOVE_PERMISSON_FROM_ROLE_REQUEST = createAction('REMOVE_PERMISSON_FROM_ROLE_REQUEST');
export const REMOVE_PERMISSON_FROM_ROLE_RECEIVE = createAction('REMOVE_PERMISSON_FROM_ROLE_RECEIVE');
export const REMOVE_PERMISSON_FROM_ROLE_FAILURE = createAction('REMOVE_PERMISSON_FROM_ROLE_FAILURE');
export const PERMISSIONS_REQUEST = createAction('PERMISSIONS_REQUEST');
export const PERMISSIONS_RECEIVED = createAction('PERMISSIONS_RECEIVED');
export const PERMISSIONS_ERROR = createAction('PERMISSIONS_ERROR');
export const PERMISSION_UPDATE = createAction('PERMISSION_UPDATE');
export const PERMISSION_CLEAR = createAction('PERMISSION_CLEAR');
export const PERMISSION_MODIFY = createAction('PERMISSION_MODIFY');
export const PERMISSION_ADD = createAction('PERMISSION_ADD');
export const FORMFIELD_UPDATE = createAction('FORMFIELD_UPDATE');
