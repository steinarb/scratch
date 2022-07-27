import { createAction } from '@reduxjs/toolkit';

export const LOGIN_HENT = createAction('LOGIN_HENT');
export const LOGIN_MOTTA = createAction('LOGIN_MOTTA');
export const LOGIN_ERROR = createAction('LOGIN_ERROR');

export const LOGOUT_HENT = createAction('LOGOUT_HENT');
export const LOGOUT_MOTTA = createAction('LOGOUT_MOTTA');
export const LOGOUT_ERROR = createAction('LOGOUT_ERROR');

export const LOGINTILSTAND_HENT = createAction('LOGINTILSTAND_HENT');
export const LOGINTILSTAND_MOTTA = createAction('LOGINTILSTAND_MOTTA');
export const LOGINTILSTAND_ERROR = createAction('LOGINTILSTAND_ERROR');

export const OVERSIKT_HENT = createAction('OVERSIKT_HENT');
export const OVERSIKT_MOTTA = createAction('OVERSIKT_MOTTA');
export const OVERSIKT_ERROR = createAction('OVERSIKT_ERROR');

export const VELG_BUTIKK = createAction('VELG_BUTIKK');
export const VALGT_BUTIKK = createAction('VALGT_BUTIKK');

export const BUTIKKNAVN_ENDRE = createAction('BUTIKKNAVN_ENDRE');

export const BELOP_ENDRE = createAction('BELOP_ENDRE');
export const HOME_BUTIKKNAVN_ENDRE = createAction('HOME_BUTIKKNAVN_ENDRE');
export const HOME_VELG_BUTIKK = createAction('HOME_VELG_BUTIKK');
export const DATO_ENDRE = createAction('DATO_ENDRE');

export const NYHANDLING_REGISTRER = createAction('NYHANDLING_REGISTRER');
export const NYHANDLING_LAGRET = createAction('NYHANDLING_LAGRET');
export const NYHANDLING_ERROR = createAction('NYHANDLING_ERROR');

export const NYBUTIKK_REGISTRER = createAction('NYBUTIKK_REGISTRER');
export const NYBUTIKK_LAGRET = createAction('NYBUTIKK_LAGRET');
export const NYBUTIKK_ERROR = createAction('NYBUTIKK_ERROR');

export const BUTIKK_LAGRE = createAction('BUTIKK_LAGRE');
export const BUTIKK_LAGRET = createAction('BUTIKK_LAGRET');

export const BUTIKKER_HENT = createAction('BUTIKKER_HENT');
export const BUTIKKER_MOTTA = createAction('BUTIKKER_MOTTA');
export const BUTIKKER_ERROR = createAction('BUTIKKER_ERROR');

export const HANDLINGER_HENT = createAction('HANDLINGER_HENT');
export const HANDLINGER_MOTTA = createAction('HANDLINGER_MOTTA');
export const HANDLINGER_ERROR = createAction('HANDLINGER_ERROR');

export const SISTEHANDEL_HENT = createAction('SISTEHANDEL_HENT');
export const SISTEHANDEL_MOTTA = createAction('SISTEHANDEL_MOTTA');
export const SISTEHANDEL_ERROR = createAction('SISTEHANDEL_ERROR');

export const HANDLINGERBUTIKK_HENT = createAction('HANDLINGERBUTIKK_HENT');
export const HANDLINGERBUTIKK_MOTTA = createAction('HANDLINGERBUTIKK_MOTTA');
export const HANDLINGERBUTIKK_ERROR = createAction('HANDLINGERBUTIKK_ERROR');

export const SUMBUTIKK_HENT = createAction('SUMBUTIKK_HENT');
export const SUMBUTIKK_MOTTA = createAction('SUMBUTIKK_MOTTA');
export const SUMBUTIKK_ERROR = createAction('SUMBUTIKK_ERROR');

export const SUMYEAR_HENT = createAction('SUMYEAR_HENT');
export const SUMYEAR_MOTTA = createAction('SUMYEAR_MOTTA');
export const SUMYEAR_ERROR = createAction('SUMYEAR_ERROR');

export const SUMYEARMONTH_HENT = createAction('SUMYEARMONTH_HENT');
export const SUMYEARMONTH_MOTTA = createAction('SUMYEARMONTH_MOTTA');
export const SUMYEARMONTH_ERROR = createAction('SUMYEARMONTH_ERROR');

export const FAVORITTER_HENT = createAction('FAVORITTER_HENT');
export const FAVORITTER_MOTTA = createAction('FAVORITTER_MOTTA');
export const FAVORITTER_ERROR = createAction('FAVORITTER_ERROR');

export const VELG_FAVORITTBUTIKK = createAction('VELG_FAVORITTBUTIKK');

export const LEGG_TIL_FAVORITT = createAction('LEGG_TIL_FAVORITT');
export const LEGG_TIL_FAVORITT_ERROR = createAction('LEGG_TIL_FAVORITT_ERROR');

export const SLETT_FAVORITT = createAction('SLETT_FAVORITT');
export const SLETT_FAVORITT_ERROR = createAction('SLETT_FAVORITT_ERROR');

export const BYTT_FAVORITTER = createAction('BYTT_FAVORITTER');
export const BYTT_FAVORITTER_ERROR = createAction('BYTT_FAVORITTER_ERROR');

export const VIS_KVITTERING = createAction('VIS_KVITTERING');
