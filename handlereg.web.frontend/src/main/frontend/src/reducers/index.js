import { combineReducers } from 'redux';
import { connectRouter } from 'connected-react-router';
import username from './usernameReducer';
import loginresultat from './loginresultatReducer';
import oversikt from './oversiktReducer';
import butikker from './butikkerReducer';
import butikk from './butikkReducer';
import handlinger from './handlingerReducer';
import nyhandling from './nyhandlingReducer';
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
    username,
    loginresultat,
    oversikt,
    butikker,
    butikk,
    handlinger,
    nyhandling,
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
