import { takeLatest, put, select } from 'redux-saga/effects';
import {
    HOME_BUTIKKNAVN_ENDRE,
    HOME_VELG_BUTIKK,
} from '../actiontypes';

function* changeStoreIdWhenButikknavnMatchesButikk(action) {
    const storeId = yield select(state => (state.butikker.find(b => b.butikknavn === action.payload) || {}).storeId);
    if (storeId) {
        yield put(HOME_VELG_BUTIKK(storeId));
    }
}

export default function* butikknavnSaga() {
    yield takeLatest(HOME_BUTIKKNAVN_ENDRE, changeStoreIdWhenButikknavnMatchesButikk);
}
