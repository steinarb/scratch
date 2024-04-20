import 'regenerator-runtime/runtime';
import { fork, all } from "redux-saga/effects";
import allroutes from './allroutesSaga';
import updateAllroutesSaga from './updateAllroutesSaga';
import albumSaga from './albumSaga';
import modifyAlbumSaga from './modifyAlbumSaga';
import addAlbumSaga from './addAlbumSaga';
import selectionSaga from './selectionSaga';
import pictureSaga from './pictureSaga';
import batchAddPicturesSaga from './batchAddPicturesSaga';
import sortAlbumentriesByDateSaga from './sortAlbumentriesByDateSaga';
import modifyPicture from './modifyPictureSaga';
import addPicture from './addPictureSaga';
import togglepasswordprotectionSaga from './togglepasswordprotectionSaga';
import imageMetadata from './imageMetadataSaga';
import deleteSaga from './deleteSaga';
import deleteSelectionSaga from './deleteSelectionSaga';
import movealbumentry from './movealbumentrySaga';
import login from './loginSaga';
import logout from './logoutSaga';
import clearOriginalRequestUrlSaga from './clearOriginalRequestUrlSaga';
import localeSaga from './localeSaga';
import defaultLocaleSaga from './defaultLocaleSaga';
import availableLocalesSaga from './availableLocalesSaga';
import displayTextsSaga from './displayTextsSaga';
import location from './locationSaga';
import showEditControlsSaga from './showEditControlsSaga';
import editModeSaga from './editModeSaga';
import albumGroupByYearSaga from './albumGroupByYearSaga';
import childentriesByYearSaga from './childentriesByYearSaga';
import modifyFailedErrorSaga from './modifyFailedErrorSaga';
import reloadShiroConfigSaga from './reloadShiroConfigSaga';
import messageBannerSaga from './messageBannerSaga';
import copyLinkSaga from './copyLinkSaga';
import reloadWebappSaga from './reloadWebappSaga';

export default function* rootSaga() {
    yield all([
        fork(allroutes),
        fork(updateAllroutesSaga),
        fork(albumSaga),
        fork(modifyAlbumSaga),
        fork(addAlbumSaga),
        fork(batchAddPicturesSaga),
        fork(sortAlbumentriesByDateSaga),
        fork(pictureSaga),
        fork(selectionSaga),
        fork(modifyPicture),
        fork(addPicture),
        fork(togglepasswordprotectionSaga),
        fork(imageMetadata),
        fork(deleteSaga),
        fork(deleteSelectionSaga),
        fork(movealbumentry),
        fork(login),
        fork(logout),
        fork(clearOriginalRequestUrlSaga),
        fork(localeSaga),
        fork(defaultLocaleSaga),
        fork(availableLocalesSaga),
        fork(displayTextsSaga),
        fork(location),
        fork(showEditControlsSaga),
        fork(editModeSaga),
        fork(albumGroupByYearSaga),
        fork(childentriesByYearSaga),
        fork(modifyFailedErrorSaga),
        fork(reloadShiroConfigSaga),
        fork(messageBannerSaga),
        fork(copyLinkSaga),
        fork(reloadWebappSaga),
    ]);
}
