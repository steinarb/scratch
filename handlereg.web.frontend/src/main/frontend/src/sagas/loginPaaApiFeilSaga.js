import { takeLatest, debounce, put, select } from 'redux-saga/effects';
import {
    REST_API_FEILER_FORDI_IKKE_INNLOGGET,
    REST_API_FEILER_FORDI_MANGLENDE_TILGANG,
    OVERSIKT_ERROR,
    NYHANDLING_ERROR,
    NYBUTIKK_ERROR,
    BUTIKKER_ERROR,
    HANDLINGER_ERROR,
    SISTEHANDEL_ERROR,
    HANDLINGERBUTIKK_ERROR,
    SUMBUTIKK_ERROR,
    SUMYEAR_ERROR,
    SUMYEARMONTH_ERROR,
    FAVORITTER_ERROR,
    LEGG_TIL_FAVORITT_ERROR,
    SLETT_FAVORITT_ERROR,
    BYTT_FAVORITTER_ERROR,
} from '../actiontypes';

export default function* loginPaaApiFeilSaga() {
    yield takeLatest(REST_API_FEILER_FORDI_IKKE_INNLOGGET, reloadPageInBrowserToHaveShiroRedirectToLogin);
    yield takeLatest(REST_API_FEILER_FORDI_MANGLENDE_TILGANG, reloadPageInBrowserToHaveShiroRedirectToUnauthorized);
    yield debounce(1000, [
        OVERSIKT_ERROR,
        NYHANDLING_ERROR,
        NYBUTIKK_ERROR,
        BUTIKKER_ERROR,
        HANDLINGER_ERROR,
        SISTEHANDEL_ERROR,
        HANDLINGERBUTIKK_ERROR,
        SUMBUTIKK_ERROR,
        SUMYEAR_ERROR,
        SUMYEARMONTH_ERROR,
        FAVORITTER_ERROR,
        LEGG_TIL_FAVORITT_ERROR,
        SLETT_FAVORITT_ERROR,
        BYTT_FAVORITTER_ERROR,
    ], finnTypeFeil);
}

function* finnTypeFeil(action) {
    const responseCode = action.payload.response.status;
    if (responseCode === 401) {
        yield put(REST_API_FEILER_FORDI_IKKE_INNLOGGET());
    } else if (responseCode === 403) {
        yield put(REST_API_FEILER_FORDI_MANGLENDE_TILGANG());
    }
}

function* reloadPageInBrowserToHaveShiroRedirectToLogin() {
    const basename = yield select(state => state.basename);
    location.href = basename + '/';
}

function* reloadPageInBrowserToHaveShiroRedirectToUnauthorized() {
    const basename = yield select(state => state.basename);
    location.href = basename + '/';
}
