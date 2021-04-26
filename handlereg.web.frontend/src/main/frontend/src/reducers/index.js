import { combineReducers } from 'redux';
import { connectRouter } from 'connected-react-router';
import { emptyUser, emptyUserAndPasswords, emptyRole, emptyPermission } from '../constants';
import login from './login';
import oversikt from './oversikt';
import butikker from './butikker';
import butikk from './butikk';
import valgtButikk from './valgtButikk';
import handlinger from './handlinger';
import nyhandling from './nyhandling';
import sumbutikk from './sumbutikk';
import handlingerbutikk from './handlingerbutikk';
import sistehandel from './sistehandel';
import sumyear from './sumyear';
import sumyearmonth from './sumyearmonth';
import brukernavn from './brukernavnReducer';
import favoritter from './favoritterReducer';
import favorittbutikk from './favorittbutikkReducer';
import errors from './errors';

export default (history) => combineReducers({
    router: connectRouter(history),
    login: login.reducer,
    oversikt: oversikt.reducer,
    butikker: butikker.reducer,
    butikk: butikk.reducer,
    handlinger: handlinger.reducer,
    nyhandling: nyhandling.reducer,
    sumbutikk: sumbutikk.reducer,
    handlingerbutikk: handlingerbutikk.reducer,
    sistehandel: sistehandel.reducer,
    sumyear: sumyear.reducer,
    sumyearmonth: sumyearmonth.reducer,
    errors: errors.reducer,
    brukernavn,
    favoritter,
    favorittbutikk,
});
