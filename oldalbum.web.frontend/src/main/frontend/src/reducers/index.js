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
import loginresult from './loginresultReducer';
import username from './usernameReducer';
import password from './passwordReducer';

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
    loginresult,
    username,
    password,
});
