import { combineReducers } from 'redux';
import loginresultat from './loginresultatReducer';
import { api } from '../api';
import butikk from './butikkReducer';
import butikknavn from './butikknavnReducer';
import storeId from './storeIdReducer';
import handletidspunkt from './handletidspunktReducer';
import belop from './belopReducer';
import viskvittering from './viskvitteringReducer';
import favorittbutikk from './favorittbutikkReducer';
import basename from './basenameReducer';

export default (routerReducer) => combineReducers({
    router: routerReducer,
    loginresultat,
    [api.reducerPath]: api.reducer,
    butikk,
    butikknavn,
    storeId,
    handletidspunkt,
    belop,
    viskvittering,
    favorittbutikk,
    basename,
});
