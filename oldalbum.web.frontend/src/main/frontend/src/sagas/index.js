import 'regenerator-runtime/runtime';
import { fork, all } from "redux-saga/effects";
import allroutes from './allroutesSaga';
import login from './loginSaga';
import logout from './logoutSaga';

export default function* rootSaga() {
    yield all([
        fork(allroutes),
        fork(login),
        fork(logout),
    ]);
};
