import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    UPDATE_JOB_REQUEST,
    UPDATE_JOB_RECEIVE,
    UPDATE_JOB_FAILURE,
} from '../actiontypes';

// watcher saga
export function* requestUpdateJobSaga() {
    yield takeLatest(UPDATE_JOB_REQUEST, receiveUpdateJobSaga);
}

function doUpdateJob(updatedJob) {
    return axios.post('/ukelonn/api/job/update', updatedJob);
}

// worker saga
function* receiveUpdateJobSaga(action) {
    try {
        const response = yield call(doUpdateJob, action.payload);
        const jobs = (response.headers['content-type'] == 'application/json') ? response.data : [];
        yield put(UPDATE_JOB_RECEIVE(jobs));
    } catch (error) {
        yield put(UPDATE_JOB_FAILURE(error));
    }
}
