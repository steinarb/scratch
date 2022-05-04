import { combineReducers } from 'redux';
import { connectRouter } from 'connected-react-router';
import alert from './alertReducer';
import allroutes from './allroutesReducer';
import albumentries from './albumentriesReducer';
import childentries from './childentriesReducer';
import previousentry from './previousentryReducer';
import nextentry from './nextentryReducer';
import modifyalbum from './modifyalbumReducer';
import addalbum from './addalbumReducer';
import modifypicture from './modifypictureReducer';
import addpicture from './addpictureReducer';
import errors from './errorsReducer';
import haveReceivedInitialLoginStatus from './haveReceivedInitialLoginStatusReducer';
import loginresult from './loginresultReducer';

export default (history) => combineReducers({
    router: connectRouter(history),
    alert,
    allroutes,
    albumentries,
    childentries,
    previousentry,
    nextentry,
    modifyalbum,
    addalbum,
    modifypicture,
    addpicture,
    errors,
    haveReceivedInitialLoginStatus,
    loginresult,
});
