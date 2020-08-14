import 'regenerator-runtime/runtime';
import { fork, all } from "redux-saga/effects";
import allroutes from './allroutesSaga';
import modifyalbum from './modifyalbumSaga';
import addalbum from './addalbumSaga';
import modifypicture from './modifypictureSaga';
import addpicture from './addpictureSaga';
import deleteSaga from './deleteSaga';
import movealbumentry from './movealbumentrySaga';
import login from './loginSaga';
import logout from './logoutSaga';
import location from './locationSaga';

export default function* rootSaga() {
    yield all([
        fork(allroutes),
        fork(modifyalbum),
        fork(addalbum),
        fork(modifypicture),
        fork(addpicture),
        fork(deleteSaga),
        fork(movealbumentry),
        fork(login),
        fork(logout),
        fork(location),
    ]);
};
