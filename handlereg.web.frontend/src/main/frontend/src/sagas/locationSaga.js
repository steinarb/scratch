import { takeLatest, put } from 'redux-saga/effects';
import { LOCATION_CHANGE } from 'connected-react-router';
import {
    OVERSIKT_HENT,
    BUTIKKER_HENT,
    SUMBUTIKK_HENT,
    HANDLINGERBUTIKK_HENT,
    SISTEHANDEL_HENT,
    SUMYEAR_HENT,
    SUMYEARMONTH_HENT,
    VIS_KVITTERING,
} from '../actiontypes';

function* locationChange(action) {
    const { location = {} } = action.payload || {};
    const { pathname = '' } = location;

    if (pathname === '/handlereg/') {
        yield put(OVERSIKT_HENT());
        yield put(BUTIKKER_HENT());
    }

    if (pathname === '/handlereg/hurtigregistrering') {
        yield put(OVERSIKT_HENT());
        yield put(BUTIKKER_HENT());
        yield put(VIS_KVITTERING(false));
    }

    if (pathname === '/handlereg/endrebutikk') {
        yield put(BUTIKKER_HENT());
    }

    if (pathname === '/handlereg/statistikk/sumbutikk') {
        yield put(SUMBUTIKK_HENT());
    }

    if (pathname === '/handlereg/statistikk/handlingerbutikk') {
        yield put(HANDLINGERBUTIKK_HENT());
    }

    if (pathname === '/handlereg/statistikk/sistehandel') {
        yield put(SISTEHANDEL_HENT());
    }

    if (pathname === '/handlereg/statistikk/sumyear') {
        yield put(SUMYEAR_HENT());
    }

    if (pathname === '/handlereg/statistikk/sumyearmonth') {
        yield put(SUMYEARMONTH_HENT());
    }

    if (pathname === '/handlereg/favoritter/leggtil') {
        yield put(OVERSIKT_HENT());
        yield put(BUTIKKER_HENT());
    }

    if (pathname === '/handlereg/favoritter/slett') {
        yield put(OVERSIKT_HENT());
    }

    if (pathname === '/handlereg/favoritter/sorter') {
        yield put(OVERSIKT_HENT());
    }
}

export default function* locationSaga() {
    yield takeLatest(LOCATION_CHANGE, locationChange);
}
