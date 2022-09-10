import { takeLatest, put, select } from 'redux-saga/effects';
import { LOCATION_CHANGE } from 'redux-first-history';
import { parse } from 'qs';
import {
    CLEAR_ALERT,
    FILL_MODIFY_ALBUM_FORM,
    FILL_ADD_ALBUM_FORM,
    FILL_MODIFY_PICTURE_FORM,
    FILL_ADD_PICTURE_FORM,
} from '../reduxactions';

function* locationChange(action) {
    const { location = {} } = action.payload || {};
    const basename = yield select(state => state.router.basename);
    const pathname = findPathname(location, basename);
    console.log('locationChange(1)');
    console.log(basename);
    console.log(pathname);

    yield put(CLEAR_ALERT());

    if (pathname === '/modifyalbum') {
        const queryParams = parse(location.search, { ignoreQueryPrefix: true });
        const { id } = queryParams;
        const albumentries = yield select(findAlbumentries);
        const idInt = parseInt(id, 10);
        const album = albumentries[idInt];
        yield put(FILL_MODIFY_ALBUM_FORM(album));
    }

    if (pathname === '/addalbum') {
        const queryParams = parse(location.search, { ignoreQueryPrefix: true });
        const { parent } = queryParams;
        const albumentries = yield select(findAlbumentries);
        const parentId = parseInt(parent, 10);
        const parentalbum = albumentries[parentId];
        const path = (parentalbum.path ? parentalbum.path : '/') + '/';
        const sort = (parentalbum.childcount || 0) + 1;
        const title = '';
        const description = '';

        yield put(FILL_ADD_ALBUM_FORM({ parent: parentId, path, album: true, title, description, sort }));
    }

    if (pathname === '/modifypicture') {
        const queryParams = parse(location.search, { ignoreQueryPrefix: true });
        const { id } = queryParams;
        const albumentries = yield select(findAlbumentries);
        const idInt = parseInt(id, 10);
        const picture = albumentries[idInt];
        yield put(FILL_MODIFY_PICTURE_FORM(picture));
    }

    if (pathname === '/addpicture') {
        const queryParams = parse(location.search, { ignoreQueryPrefix: true });
        const { parent } = queryParams;
        const albumentries = yield select(findAlbumentries);
        const parentId = parseInt(parent, 10);
        const parentalbum = albumentries[parentId];
        const path = parentalbum.path || '';
        const sort = (parentalbum.childcount || 0) + 1;
        const basename = '';
        const title = '';
        const description = '';
        const imageUrl = '';
        const thumbnailUrl = '';

        yield put(FILL_ADD_PICTURE_FORM({ parent: parentId, path, album: false, basename, title, description, imageUrl, thumbnailUrl, sort }));
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

function findAlbumentries(state) {
    return state.albumentries || [];
}
