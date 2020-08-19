import { takeLatest, call, put, select } from 'redux-saga/effects';
import axios from 'axios';
import {
    DELETE_ITEM,
    ALLROUTES_RECEIVE,
    DELETE_ITEM_ERROR,
} from '../reduxactions';

function deleteAlbumItem(item) {
    return axios.post('/api/deleteentry', item);
}

function* deleteItemAndReceiveRoutes(action) {
    try {
        const response = yield call(deleteAlbumItem, action.payload);
        const routes = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(ALLROUTES_RECEIVE(routes));
    } catch (error) {
        yield put(DELETE_ITEM_ERROR(error));
    }
}

export default function* deleteSaga() {
    yield takeLatest(DELETE_ITEM, deleteItemAndReceiveRoutes);
}
