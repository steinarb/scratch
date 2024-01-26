import { takeLatest, select, put } from 'redux-saga/effects';
import {
    ALLROUTES_RECEIVE,
    UPDATE_ALLROUTES,
    SET_CHILDENTRIES_BY_YEAR,
} from '../reduxactions';

export default function* childentriesByYearSaga() {
    yield takeLatest(ALLROUTES_RECEIVE, createMapFromIdToMapOfYearWithArrayOfChildren);
    yield takeLatest(UPDATE_ALLROUTES, createMapFromIdToMapOfYearWithArrayOfChildren);
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
