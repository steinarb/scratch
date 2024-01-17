import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    DELETE_SELECTION_REQUEST,
    DELETE_SELECTION_RECEIVE,
    DELETE_SELECTION_FAILURE,
} from '../reduxactions';

function deleteAlbumItemSelection(selection) {
    return axios.post('/api/deleteselection', selection);
}

function* deleteSelectionAndReceiveRoutes(action) {
    try {
        const selection = action.payload.map(e => e.id);
        const response = yield call(deleteAlbumItemSelection, selection);
        const routes = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(DELETE_SELECTION_RECEIVE(routes));
    } catch (error) {
        yield put(DELETE_SELECTION_FAILURE(error));
    }
}

export default function* deleteSaga() {
    yield takeLatest(DELETE_SELECTION_REQUEST, deleteSelectionAndReceiveRoutes);
}
