import { createAction } from '@reduxjs/toolkit';

export const VELG_BUTIKK = createAction('VELG_BUTIKK');

export const BUTIKKNAVN_ENDRE = createAction('BUTIKKNAVN_ENDRE');

export const BELOP_ENDRE = createAction('BELOP_ENDRE');
export const HOME_BUTIKKNAVN_ENDRE = createAction('HOME_BUTIKKNAVN_ENDRE');
export const HOME_VELG_BUTIKK = createAction('HOME_VELG_BUTIKK');
export const DATO_ENDRE = createAction('DATO_ENDRE');

export const VELG_FAVORITTBUTIKK = createAction('VELG_FAVORITTBUTIKK');

export const VIS_KVITTERING = createAction('VIS_KVITTERING');

export const SET_BASENAME = createAction('SET_BASENAME');
