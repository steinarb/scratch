import { takeLatest, select } from 'redux-saga/effects';
import Cookies from 'js-cookie';
import {
    SET_ALBUM_GROUP_BY_YEAR,
    UNSET_ALBUM_GROUP_BY_YEAR,
} from '../reduxactions';

export default function* albumGroupByYearSaga() {
    yield takeLatest(SET_ALBUM_GROUP_BY_YEAR, updateEditModeCookie);
    yield takeLatest(UNSET_ALBUM_GROUP_BY_YEAR, updateEditModeCookie);
}

function* updateEditModeCookie() {
    const albumGroupByYear = yield select(state => state.albumGroupByYear);
    yield Cookies.set('albumGroupByYear', JSON.stringify(albumGroupByYear));
}
