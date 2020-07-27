import 'regenerator-runtime/runtime';
import { fork, all } from "redux-saga/effects";
import allroutes from './allroutesSaga';

export default function* rootSaga() {
    yield all([
        fork(allroutes),
    ]);
};
