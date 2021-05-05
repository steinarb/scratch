import 'regenerator-runtime/runtime';
import { fork, all } from "redux-saga/effects";
import loginSaga from './loginSaga';
import logoutSaga from './logoutSaga';
import logintilstandSaga from './logintilstandSaga';
import locationSaga from './locationSaga';
import oversiktSaga from './oversiktSaga';
import handlingerSaga from './handlingerSaga';
import nyhandlingSaga from './nyhandlingSaga';
import butikkerSaga from './butikkerSaga';
import nybutikkSaga from './nybutikkSaga';
import lagrebutikkSaga from './lagrebutikkSaga';
import sumbutikkSaga from './sumbutikkSaga';
import handlingerbutikkSaga from './handlingerbutikkSaga';
import sistehandelSaga from './sistehandelSaga';
import sumyearSaga from './sumyearSaga';
import sumyearmonthSaga from './sumyearmonthSaga';
import favoritterSaga from './favoritterSaga';
import favorittLeggTilSaga from './favorittLeggTilSaga';
import favorittSlettSaga from './favorittSlettSaga';
import favoritterByttSaga from './favoritterByttSaga';

export default function* rootSaga() {
    yield all([
        fork(loginSaga),
        fork(logoutSaga),
        fork(logintilstandSaga),
        fork(locationSaga),
        fork(oversiktSaga),
        fork(handlingerSaga),
        fork(nyhandlingSaga),
        fork(butikkerSaga),
        fork(nybutikkSaga),
        fork(lagrebutikkSaga),
        fork(sumbutikkSaga),
        fork(handlingerbutikkSaga),
        fork(sistehandelSaga),
        fork(sumyearSaga),
        fork(sumyearmonthSaga),
        fork(favoritterSaga),
        fork(favorittLeggTilSaga),
        fork(favorittSlettSaga),
        fork(favoritterByttSaga),
    ]);
}
