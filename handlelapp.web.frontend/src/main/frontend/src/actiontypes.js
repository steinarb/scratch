import { createAction } from '@reduxjs/toolkit';

export const USERNAME_ENDRE = createAction('USERNAME_ENDRE');
export const PASSWORD_ENDRE = createAction('PASSWORD_ENDRE');

export const LOGIN_HENT = createAction('LOGIN_HENT');
export const LOGIN_MOTTA = createAction('LOGIN_MOTTA');
export const LOGIN_ERROR = createAction('LOGIN_ERROR');

export const LOGOUT_HENT = createAction('LOGOUT_HENT');
export const LOGOUT_MOTTA = createAction('LOGOUT_MOTTA');
export const LOGOUT_ERROR = createAction('LOGOUT_ERROR');

export const LOGINTILSTAND_HENT = createAction('LOGINTILSTAND_HENT');
export const LOGINTILSTAND_MOTTA = createAction('LOGINTILSTAND_MOTTA');
export const LOGINTILSTAND_ERROR = createAction('LOGINTILSTAND_ERROR');
