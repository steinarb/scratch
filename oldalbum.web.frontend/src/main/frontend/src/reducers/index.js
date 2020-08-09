import { combineReducers } from 'redux';
import { connectRouter } from 'connected-react-router';
import allroutes from './allroutesReducer';
import albumentries from './albumentriesReducer';
import childentries from './childentriesReducer';
import modifyalbum from './modifyalbumReducer';
import addalbum from './addalbumReducer';
import modifypicture from './modifypictureReducer';
import errors from './errorsReducer';
import login from './loginReducer';

export default (history) => combineReducers({
    router: connectRouter(history),
    allroutes,
    albumentries,
    childentries,
    modifyalbum,
    addalbum,
    modifypicture,
    errors,
    login,
});
