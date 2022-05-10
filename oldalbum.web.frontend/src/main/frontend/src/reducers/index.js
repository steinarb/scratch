import { combineReducers } from 'redux';
import { connectRouter } from 'connected-react-router';
import alert from './alertReducer';
import allroutes from './allroutesReducer';
import albumentries from './albumentriesReducer';
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
import albumentrySort from './albumentrySortReducer';
import errors from './errorsReducer';
import haveReceivedInitialLoginStatus from './haveReceivedInitialLoginStatusReducer';
import loggedIn from './loggedInReducer';
import username from './usernameReducer';
import logstatusMessage from './logstatusMessageReducer';
import canModifyAlbum from './canModifyAlbumReducer';
import canLogin from './canLoginReducer';

export default (history) => combineReducers({
    router: connectRouter(history),
    alert,
    allroutes,
    albumentries,
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
    albumentrySort,
    errors,
    haveReceivedInitialLoginStatus,
    loggedIn,
    username,
    logstatusMessage,
    canModifyAlbum,
    canLogin,
});
