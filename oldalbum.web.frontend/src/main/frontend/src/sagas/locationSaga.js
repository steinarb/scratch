import { takeLatest, put, select } from 'redux-saga/effects';
import { LOCATION_CHANGE } from 'connected-react-router';
import { parse } from 'qs';
import {
    MODIFY_ALBUM,
    ADD_ALBUM,
    MODIFY_PICTURE,
    ADD_PICTURE,
} from '../reduxactions';

function* locationChange(action) {
    const { location = {} } = action.payload || {};
    const { pathname = '', search = '' } = location;

    if (pathname === '/modifyalbum') {
        const queryParams = parse(location.search, { ignoreQueryPrefix: true });
        const { id } = queryParams;
        const albumentries = yield select(findAlbumentries);
        const idInt = parseInt(id, 10);
        const album = albumentries[idInt];

        yield put(MODIFY_ALBUM(album || { id: idInt } ));
    }

    if (pathname === '/addalbum') {
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

        yield put(ADD_ALBUM({ parent: parentId, path, album: true, basename, title, description, sort }));
    }

    if (pathname === '/modifypicture') {
        const queryParams = parse(location.search, { ignoreQueryPrefix: true });
        const { id } = queryParams;
        const albumentries = yield select(findAlbumentries);
        const idInt = parseInt(id, 10);
        const picture = albumentries[idInt];

        yield put(MODIFY_PICTURE(picture || { id: idInt } ));
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

        yield put(ADD_PICTURE({ parent: parentId, path, album: false, basename, title, description, imageUrl, thumbnailUrl, sort }));
    }
}

export default function* locationSaga() {
    yield takeLatest(LOCATION_CHANGE, locationChange);
}

function findAlbumentries(state) {
    return state.albumentries || new Map([]);
}
