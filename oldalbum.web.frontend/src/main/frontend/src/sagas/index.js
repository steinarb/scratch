import 'regenerator-runtime/runtime';
import { fork, all } from "redux-saga/effects";
import allroutes from './allroutesSaga';
import albumSaga from './albumSaga';
import modifyAlbumSaga from './modifyAlbumSaga';
import addAlbumSaga from './addAlbumSaga';
import pictureSaga from './pictureSaga';
import batchAddPicturesSaga from './batchAddPicturesSaga';
import sortAlbumentriesByDateSaga from './sortAlbumentriesByDateSaga';
import modifyPicture from './modifyPictureSaga';
import addPicture from './addPictureSaga';
import imageMetadata from './imageMetadataSaga';
import deleteSaga from './deleteSaga';
import movealbumentry from './movealbumentrySaga';
import login from './loginSaga';
import logout from './logoutSaga';
import localeSaga from './localeSaga';
import defaultLocaleSaga from './defaultLocaleSaga';
import availableLocalesSaga from './availableLocalesSaga';
import displayTextsSaga from './displayTextsSaga';
import location from './locationSaga';
import showEditControlsSaga from './showEditControlsSaga';
import editModeSaga from './editModeSaga';

export default function* rootSaga() {
    yield all([
        fork(allroutes),
        fork(albumSaga),
        fork(modifyAlbumSaga),
        fork(addAlbumSaga),
        fork(batchAddPicturesSaga),
        fork(sortAlbumentriesByDateSaga),
        fork(pictureSaga),
        fork(modifyPicture),
        fork(addPicture),
        fork(imageMetadata),
        fork(deleteSaga),
        fork(movealbumentry),
        fork(login),
        fork(logout),
        fork(localeSaga),
        fork(defaultLocaleSaga),
        fork(availableLocalesSaga),
        fork(displayTextsSaga),
        fork(location),
        fork(showEditControlsSaga),
        fork(editModeSaga),
    ]);
}
