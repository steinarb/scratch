import { takeLatest, put, select } from 'redux-saga/effects';
import { LOCATION_CHANGE } from 'redux-first-history';
import {
    OVERSIKT_HENT,
    BUTIKKER_HENT,
    FAVORITTER_HENT,
    SUMBUTIKK_HENT,
    HANDLINGERBUTIKK_HENT,
    SISTEHANDEL_HENT,
    SUMYEAR_HENT,
    SUMYEARMONTH_HENT,
    VIS_KVITTERING,
} from '../actiontypes';

function* locationChange(action) {
    const { location = {} } = action.payload || {};
    const basename = yield select(state => state.router.basename);
    const pathname = findPathname(location, basename);

    if (pathname === '' || pathname === '/') {
        yield put(OVERSIKT_HENT());
        yield put(BUTIKKER_HENT());
    }

    if (pathname === '/hurtigregistrering') {
        yield put(OVERSIKT_HENT());
        yield put(BUTIKKER_HENT());
        yield put(VIS_KVITTERING(false));
        const brukernavn = yield select (state => (state.loginresultat || {}).brukernavn);
        if (brukernavn) {
            yield put(FAVORITTER_HENT());
        }
    }

    if (pathname === '/endrebutikk') {
        yield put(BUTIKKER_HENT());
    }

    if (pathname === '/statistikk/sumbutikk') {
        yield put(SUMBUTIKK_HENT());
    }

    if (pathname === '/statistikk/handlingerbutikk') {
        yield put(HANDLINGERBUTIKK_HENT());
    }

    if (pathname === '/statistikk/sistehandel') {
        yield put(SISTEHANDEL_HENT());
    }

    if (pathname === '/statistikk/sumyear') {
        yield put(SUMYEAR_HENT());
    }

    if (pathname === '/statistikk/sumyearmonth') {
        yield put(SUMYEARMONTH_HENT());
    }

    if (pathname === '/favoritter/leggtil') {
        yield put(OVERSIKT_HENT());
        yield put(BUTIKKER_HENT());
    }

    if (pathname === '/favoritter/slett') {
        yield put(OVERSIKT_HENT());
    }

    if (pathname === '/favoritter/sorter') {
        yield put(OVERSIKT_HENT());
    }
}

export default function* locationSaga() {
    yield takeLatest(LOCATION_CHANGE, locationChange);
}

function findPathname(location, basename) {
    if (basename === '/') {
        return location.pathname;
    }

    return location.pathname.replace(new RegExp('^' + basename + '(.*)'), '$1');
}
