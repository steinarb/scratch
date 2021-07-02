import 'regenerator-runtime/runtime';
import { fork, all } from "redux-saga/effects";
import loginSaga from './loginSaga';
import logoutSaga from './logoutSaga';
import logintilstandSaga from './logintilstandSaga';
import locationSaga from './locationSaga';

export default function* rootSaga() {
    yield all([
        fork(loginSaga),
        fork(logoutSaga),
        fork(logintilstandSaga),
        fork(locationSaga),
    ]);
}
