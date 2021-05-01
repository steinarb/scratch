import { combineReducers } from 'redux';
import { connectRouter } from 'connected-react-router';
import { emptyUser, emptyUserAndPasswords, emptyRole, emptyPermission } from '../constants';
import username from './usernameReducer';
import password from './passwordReducer';
import loginresultat from './loginresultatReducer';
import oversikt from './oversiktReducer';
import butikker from './butikkerReducer';
import butikk from './butikkReducer';
import valgtButikk from './valgtButikkReducer';
import handlinger from './handlingerReducer';
import nyhandling from './nyhandlingReducer';
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
    username,
    password,
    loginresultat,
    oversikt,
    butikker,
    butikk,
    handlinger,
    nyhandling,
    sumbutikk,
    handlingerbutikk,
    sistehandel,
    sumyear,
    sumyearmonth,
    errors,
    favoritter,
    favorittbutikk,
});
