import { takeLatest, put, select } from 'redux-saga/effects';
import { LOCATION_CHANGE } from 'connected-react-router';
import { parse } from 'qs';
import {
    MODIFY_ALBUM,
    ADD_ALBUM,
    MODIFY_PICTURE,
} from '../reduxactions';

function* locationChange(action) {
    const { location = {} } = action.payload || {};
    const { pathname = '', search = '' } = location;

    if (pathname === '/oldalbum/modifyalbum') {
        const queryParams = parse(location.search, { ignoreQueryPrefix: true });
        const { id } = queryParams;
        const albumentries = yield select(findAlbumentries);
        const idInt = parseInt(id, 10);
        const album = albumentries.get(idInt);

        yield put(MODIFY_ALBUM(album || { id: idInt } ));
    }

    if (pathname === '/oldalbum/addalbum') {
        const queryParams = parse(location.search, { ignoreQueryPrefix: true });
        const { parent } = queryParams;
        const albumentries = yield select(findAlbumentries);
        const parentId = parseInt(parent, 10);
        const parentalbum = albumentries.get(parentId);
        const path = parentalbum.path || '';
        const basename = '';
        const title = '';
        const description = '';

        yield put(ADD_ALBUM({ parent: parentId, path, album: true, basename, title, description }));
    }

    if (pathname === '/oldalbum/modifypicture') {
        const queryParams = parse(location.search, { ignoreQueryPrefix: true });
        const { id } = queryParams;
        const albumentries = yield select(findAlbumentries);
        const idInt = parseInt(id, 10);
        const picture = albumentries.get(idInt);

        yield put(MODIFY_PICTURE(picture || { id: idInt } ));
    }
}

export default function* locationSaga() {
    yield takeLatest(LOCATION_CHANGE, locationChange);
}

function findAlbumentries(state) {
    return state.albumentries || new Map([]);
}
