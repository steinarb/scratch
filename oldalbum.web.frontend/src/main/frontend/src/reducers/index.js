import { combineReducers } from 'redux';
import { connectRouter } from 'connected-react-router';
import allroutesReducer from './allroutesReducer';
import albumentriesReducer from './albumentriesReducer';
import errorsReducer from './errorsReducer';

export default (history) => combineReducers({
    router: connectRouter(history),
    allroutes: allroutesReducer,
    albumentries: albumentriesReducer,
    errors: errorsReducer,
});
