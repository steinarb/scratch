import { createAction } from '@reduxjs/toolkit';

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
