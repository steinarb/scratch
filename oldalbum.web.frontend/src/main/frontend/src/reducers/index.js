import { combineReducers } from 'redux';
import { connectRouter } from 'connected-react-router';
import allroutes from './allroutesReducer';
import albumentries from './albumentriesReducer';
import childentries from './childentriesReducer';
import errors from './errorsReducer';

export default (history) => combineReducers({
    router: connectRouter(history),
    allroutes,
    albumentries,
    childentries,
    errors,
});
