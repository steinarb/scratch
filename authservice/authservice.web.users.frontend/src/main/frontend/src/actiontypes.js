import { createAction } from '@reduxjs/toolkit';

export const USERS_REQUEST = createAction('USERS_REQUEST');
export const USERS_RECEIVE = createAction('USERS_RECEIVE');
export const USERS_FAILURE = createAction('USERS_FAILURE');
export const SELECT_USER = createAction('SELECT_USER');
export const SELECTED_USER = createAction('SELECTED_USER');
export const MODIFY_USERNAME = createAction('MODIFY_USERNAME');
export const MODIFY_EMAIL = createAction('MODIFY_EMAIL');
export const MODIFY_FIRSTNAME = createAction('MODIFY_FIRSTNAME');
export const MODIFY_LASTNAME = createAction('MODIFY_LASTNAME');
export const MODIFY_USER_BUTTON_CLICKED = createAction('MODIFY_USER_BUTTON_CLICKED');
export const SAVE_MODIFIED_USER_REQUEST = createAction('SAVE_MODIFIED_USER_REQUEST');
export const SAVE_MODIFIED_USER_RECEIVE = createAction('SAVE_MODIFIED_USER_RECEIVE');
export const SAVE_MODIFIED_USER_FAILURE = createAction('SAVE_MODIFIED_USER_FAILURE');
export const MODIFY_PASSWORD1 = createAction('MODIFY_PASSWORD1');
export const MODIFY_PASSWORD2 = createAction('MODIFY_PASSWORD2');
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
export const USER_UPDATE = createAction('USER_UPDATE');
export const USER_CLEAR = createAction('USER_CLEAR');
export const USER_MODIFY = createAction('USER_MODIFY');
export const USER_ADD = createAction('USER_ADD');
export const USER_ADD_ROLES = createAction('USER_ADD_ROLES');
export const USER_REMOVE_ROLES = createAction('USER_REMOVE_ROLES');
export const USERROLES_REQUEST = createAction('USERROLES_REQUEST');
export const USERROLES_RECEIVED = createAction('USERROLES_RECEIVED');
export const USERROLES_ERROR = createAction('USERROLES_ERROR');
export const ROLES_NOT_ON_USER_UPDATE = createAction('ROLES_NOT_ON_USER_UPDATE');
export const ROLES_NOT_ON_USER_SELECTED = createAction('ROLES_NOT_ON_USER_SELECTED');
export const ROLES_NOT_ON_USER_CLEAR = createAction('ROLES_NOT_ON_USER_CLEAR');
export const ROLES_ON_USER_UPDATE = createAction('ROLES_ON_USER_UPDATE');
export const ROLES_ON_USER_SELECTED = createAction('ROLES_ON_USER_SELECTED');
export const ROLES_ON_USER_CLEAR = createAction('ROLES_ON_USER_CLEAR');
export const PASSWORDS_UPDATE = createAction('PASSWORDS_UPDATE');
export const PASSWORDS_CLEAR = createAction('PASSWORDS_CLEAR');
export const PASSWORDS_MODIFY = createAction('PASSWORDS_MODIFY');
export const ROLES_REQUEST = createAction('ROLES_REQUEST');
export const ROLES_RECEIVED = createAction('ROLES_RECEIVED');
export const ROLES_ERROR = createAction('ROLES_ERROR');
export const ROLE_UPDATE = createAction('ROLE_UPDATE');
export const ROLE_CLEAR = createAction('ROLE_CLEAR');
export const ROLE_MODIFY = createAction('ROLE_MODIFY');
export const ROLE_ADD = createAction('ROLE_ADD');
export const ROLE_ADD_PERMISSIONS = createAction('ROLE_ADD_PERMISSIONS');
export const ROLE_REMOVE_PERMISSIONS = createAction('ROLE_REMOVE_PERMISSIONS');
export const ROLEPERMISSIONS_REQUEST = createAction('ROLEPERMISSIONS_REQUEST');
export const ROLEPERMISSIONS_RECEIVED = createAction('ROLEPERMISSIONS_RECEIVED');
export const ROLEPERMISSIONS_ERROR = createAction('ROLEPERMISSIONS_ERROR');
export const PERMISSIONS_NOT_ON_ROLE_UPDATE = createAction('PERMISSIONS_NOT_ON_ROLE_UPDATE');
export const PERMISSIONS_NOT_ON_ROLE_SELECTED = createAction('PERMISSIONS_NOT_ON_ROLE_SELECTED');
export const PERMISSIONS_NOT_ON_ROLE_CLEAR = createAction('PERMISSIONS_NOT_ON_ROLE_CLEAR');
export const PERMISSIONS_ON_ROLE_UPDATE = createAction('PERMISSIONS_ON_ROLE_UPDATE');
export const PERMISSIONS_ON_ROLE_SELECTED = createAction('PERMISSIONS_ON_ROLE_SELECTED');
export const PERMISSIONS_ON_ROLE_CLEAR = createAction('PERMISSIONS_ON_ROLE_CLEAR');
export const PERMISSIONS_REQUEST = createAction('PERMISSIONS_REQUEST');
export const PERMISSIONS_RECEIVED = createAction('PERMISSIONS_RECEIVED');
export const PERMISSIONS_ERROR = createAction('PERMISSIONS_ERROR');
export const PERMISSION_UPDATE = createAction('PERMISSION_UPDATE');
export const PERMISSION_CLEAR = createAction('PERMISSION_CLEAR');
export const PERMISSION_MODIFY = createAction('PERMISSION_MODIFY');
export const PERMISSION_ADD = createAction('PERMISSION_ADD');
export const FORMFIELD_UPDATE = createAction('FORMFIELD_UPDATE');
