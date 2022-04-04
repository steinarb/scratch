import { combineReducers } from 'redux';
import { connectRouter } from 'connected-react-router';
import loginresultat from './loginresultatReducer';
import oversikt from './oversiktReducer';
import butikker from './butikkerReducer';
import butikk from './butikkReducer';
import butikknavn from './butikknavnReducer';
import handlinger from './handlingerReducer';
import storeId from './storeIdReducer';
import handletidspunkt from './handletidspunktReducer';
import belop from './belopReducer';
import viskvittering from './viskvitteringReducer';
import sumbutikk from './sumbutikkReducer';
import handlingerbutikk from './handlingerbutikkReducer';
import sistehandel from './sistehandelReducer';
import sumyear from './sumyearReducer';
import sumyearmonth from './sumyearmonthReducer';
import favoritter from './favoritterReducer';
import favorittbutikk from './favorittbutikkReducer';
import errors from './errorsReducer';

export default (history) => combineReducers({
    router: connectRouter(history),
    loginresultat,
    oversikt,
    butikker,
    butikk,
    butikknavn,
    handlinger,
    storeId,
    handletidspunkt,
    belop,
    viskvittering,
    sumbutikk,
    handlingerbutikk,
    sistehandel,
    sumyear,
    sumyearmonth,
    errors,
    favoritter,
    favorittbutikk,
});
