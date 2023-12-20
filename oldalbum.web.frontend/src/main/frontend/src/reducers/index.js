import { combineReducers } from 'redux';
import alert from './alertReducer';
import allroutes from './allroutesReducer';
import albumentries from './albumentriesReducer';
import selected from './selectedReducer';
import childentries from './childentriesReducer';
import previousentry from './previousentryReducer';
import nextentry from './nextentryReducer';
import albumentryid from './albumentryidReducer';
import albumentryParent from './albumentryParentReducer';
import albumentryPath from './albumentryPathReducer';
import albumentryBasename from './albumentryBasenameReducer';
import albumentryTitle from './albumentryTitleReducer';
import albumentryDescription from './albumentryDescriptionReducer';
import albumentryImageUrl from './albumentryImageUrlReducer';
import albumentryThumbnailUrl from './albumentryThumbnailUrlReducer';
import albumentryLastModified from './albumentryLastModifiedReducer';
import albumentryContentLength from './albumentryContentLengthReducer';
import albumentryContentType from './albumentryContentTypeReducer';
import albumentryRequireLogin from './albumentryRequireLoginReducer';
import albumentrySort from './albumentrySortReducer';
import batchAddUrl from './batchAddUrlReducer';
import batchAddImportYear from './batchAddImportYearReducer';
import locale from './localeReducer';
import availableLocales from './availableLocalesReducer';
import displayTexts from './displayTextsReducer';
import errors from './errorsReducer';
import haveReceivedInitialLoginStatus from './haveReceivedInitialLoginStatusReducer';
import loggedIn from './loggedInReducer';
import username from './usernameReducer';
import sortingStatus from './sortingStatusReducer';
import logstatusMessage from './logstatusMessageReducer';
import showEditControls from './showEditControlsReducer';
import editMode from './editModeReducer';
import canModifyAlbum from './canModifyAlbumReducer';
import canLogin from './canLoginReducer';
import originalRequestUri from './originalRequestUriReducer';

export default (routerReducer) => combineReducers({
    router: routerReducer,
    alert,
    allroutes,
    albumentries,
    selected,
    childentries,
    previousentry,
    nextentry,
    albumentryid,
    albumentryParent,
    albumentryPath,
    albumentryBasename,
    albumentryTitle,
    albumentryDescription,
    albumentryImageUrl,
    albumentryThumbnailUrl,
    albumentryLastModified,
    albumentryContentLength,
    albumentryContentType,
    albumentryRequireLogin,
    albumentrySort,
    batchAddUrl,
    batchAddImportYear,
    locale,
    availableLocales,
    displayTexts,
    errors,
    haveReceivedInitialLoginStatus,
    loggedIn,
    username,
    sortingStatus,
    logstatusMessage,
    showEditControls,
    editMode,
    canModifyAlbum,
    canLogin,
    originalRequestUri,
});
