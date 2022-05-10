import 'regenerator-runtime/runtime';
import { fork, all } from "redux-saga/effects";
import allroutes from './allroutesSaga';
import albumSaga from './albumSaga';
import modifyAlbumSaga from './modifyAlbumSaga';
import addAlbumSaga from './addAlbumSaga';
import pictureSaga from './pictureSaga';
import modifyPicture from './modifyPictureSaga';
import addPicture from './addPictureSaga';
import imageMetadata from './imageMetadataSaga';
import deleteSaga from './deleteSaga';
import movealbumentry from './movealbumentrySaga';
import login from './loginSaga';
import logout from './logoutSaga';
import location from './locationSaga';

export default function* rootSaga() {
    yield all([
        fork(allroutes),
        fork(albumSaga),
        fork(modifyAlbumSaga),
        fork(addAlbumSaga),
        fork(pictureSaga),
        fork(modifyPicture),
        fork(addPicture),
        fork(imageMetadata),
        fork(deleteSaga),
        fork(movealbumentry),
        fork(login),
        fork(logout),
        fork(location),
    ]);
}
