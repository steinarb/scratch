import { createAction } from '@reduxjs/toolkit';

export const ALLROUTES_REQUEST = createAction('ALLROUTES_REQUEST');
export const ALLROUTES_RECEIVE = createAction('ALLROUTES_RECEIVE');
export const ALLROUTES_ERROR = createAction('ALLROUTES_ERROR');
export const LOGIN_REQUEST = createAction('LOGIN_REQUEST');
export const LOGIN_RECEIVE = createAction('LOGIN_RECEIVE');
export const LOGIN_ERROR = createAction('LOGIN_ERROR');
export const LOGOUT_REQUEST = createAction('LOGOUT_REQUEST');
export const LOGOUT_RECEIVE = createAction('LOGOUT_RECEIVE');
export const LOGOUT_ERROR = createAction('LOGOUT_ERROR');
export const USERNAME_MODIFY = createAction('USERNAME_MODIFY');
export const PASSWORD_MODIFY = createAction('PASSWORD_MODIFY');
