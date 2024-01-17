import { takeLatest, select, put } from 'redux-saga/effects';
import {
    ALLROUTES_RECEIVE,
    SAVE_MODIFIED_ALBUM_RECEIVE,
    SAVE_ADDED_ALBUM_RECEIVE,
    SAVE_MODIFIED_PICTURE_RECEIVE,
    SAVE_ADDED_PICTURE_RECEIVE,
    BATCH_ADD_PICTURES_FROM_URL_RECEIVE,
    SORT_ALBUM_ENTRIES_BY_DATE_RECEIVE,
    DELETE_ALBUMENTRY_RECEIVE,
    DELETE_SELECTION_RECEIVE,
    MOVE_ALBUMENTRY_UP_RECEIVE,
    MOVE_ALBUMENTRY_LEFT_RECEIVE,
    MOVE_ALBUMENTRY_DOWN_RECEIVE,
    MOVE_ALBUMENTRY_RIGHT_RECEIVE,
    SET_CHILDENTRIES_BY_YEAR,
} from '../reduxactions';

export default function* childentriesByYearSaga() {
    yield takeLatest(ALLROUTES_RECEIVE, createMapFromIdToMapOfYearWithArrayOfChildren);
    yield takeLatest(SAVE_MODIFIED_ALBUM_RECEIVE, createMapFromIdToMapOfYearWithArrayOfChildren);
    yield takeLatest(SAVE_ADDED_ALBUM_RECEIVE, createMapFromIdToMapOfYearWithArrayOfChildren);
    yield takeLatest(SAVE_MODIFIED_PICTURE_RECEIVE, createMapFromIdToMapOfYearWithArrayOfChildren);
    yield takeLatest(SAVE_ADDED_PICTURE_RECEIVE, createMapFromIdToMapOfYearWithArrayOfChildren);
    yield takeLatest(BATCH_ADD_PICTURES_FROM_URL_RECEIVE, createMapFromIdToMapOfYearWithArrayOfChildren);
    yield takeLatest(SORT_ALBUM_ENTRIES_BY_DATE_RECEIVE, createMapFromIdToMapOfYearWithArrayOfChildren);
    yield takeLatest(DELETE_ALBUMENTRY_RECEIVE, createMapFromIdToMapOfYearWithArrayOfChildren);
    yield takeLatest(DELETE_SELECTION_RECEIVE, createMapFromIdToMapOfYearWithArrayOfChildren);
    yield takeLatest(MOVE_ALBUMENTRY_UP_RECEIVE, createMapFromIdToMapOfYearWithArrayOfChildren);
    yield takeLatest(MOVE_ALBUMENTRY_LEFT_RECEIVE, createMapFromIdToMapOfYearWithArrayOfChildren);
    yield takeLatest(MOVE_ALBUMENTRY_DOWN_RECEIVE, createMapFromIdToMapOfYearWithArrayOfChildren);
    yield takeLatest(MOVE_ALBUMENTRY_RIGHT_RECEIVE, createMapFromIdToMapOfYearWithArrayOfChildren);
}


function* createMapFromIdToMapOfYearWithArrayOfChildren() {
    const allroutes = yield select(state => state.allroutes);
    const dateOfLastChildOfAlbum = yield select(state => state.dateOfLastChildOfAlbum);
    const children = {};
    allroutes.forEach(e => addChildToParent(children, e, dateOfLastChildOfAlbum));
    yield put(SET_CHILDENTRIES_BY_YEAR(children));
}

function addChildToParent(state, item, dateOfLastChildOfAlbum) {
    const { id, parent, lastModified } = item;
    const year = lastModified ?
          new Date(lastModified).getFullYear().toString() :
          dateOfLastChildOfAlbum[id] ?
          new Date(dateOfLastChildOfAlbum[id]).getFullYear().toString() :
          new Date().getFullYear().toString();
    if (parent) {
        if (parent in state) {
            if (year in state[parent]) {
                state[parent][year].push({ ...item });
            } else {
                state[parent][year] = [{ ...item }];
            }
        } else {
            state[parent] = {};
            state[parent][year] = [{ ...item }];
        }
    }
}
