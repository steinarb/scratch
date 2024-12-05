import { createListenerMiddleware } from '@reduxjs/toolkit';
import { isAnyOf } from '@reduxjs/toolkit';
import { api } from './api';
import { LOCATION_CHANGE } from 'redux-first-history';
import { VIS_KVITTERING } from './actiontypes';

const listenerMiddleware = createListenerMiddleware();

const isRejectedRequest = isAnyOf(
    api.endpoints.getButikker.matchRejected,
    api.endpoints.getHandlinger.matchRejected,
    api.endpoints.getOversikt.matchRejected,
    api.endpoints.getFavoritter.matchRejected,
    api.endpoints.getSumButikk.matchRejected,
    api.endpoints.getHandlingerButikk.matchRejected,
    api.endpoints.getSisteHandel.matchRejected,
    api.endpoints.getSumYear.matchRejected,
    api.endpoints.getSumYearMonth.matchRejected,
    api.endpoints.postEndrebutikk.matchRejected,
    api.endpoints.postNybutikk.matchRejected,
    api.endpoints.postNyhandling.matchRejected,
    api.endpoints.postFavorittLeggtil.matchRejected,
    api.endpoints.postFavorittSlett.matchRejected,
    api.endpoints.postFavorittBytt.matchRejected,
)

listenerMiddleware.startListening({
    matcher: isRejectedRequest,
    effect: ({ payload }) => {
        const { originalStatus } = payload || {};
        const statusCode = parseInt(originalStatus);
        if (statusCode === 401 || statusCode === 403) {
            location.reload(true);
        }
    }
})

listenerMiddleware.startListening({
    matcher: api.endpoints.getLogout.matchFulfilled,
    effect: (action, listenerApi) => {
        if (!action.payload.suksess) {
            const basename = listenerApi.getState().basename;
            location.href = basename + '/';
        }
    }
})

listenerMiddleware.startListening({
    type: LOCATION_CHANGE,
    effect: (action, listenerApi) => {
        listenerApi.dispatch(VIS_KVITTERING(false));
    }
})

export default listenerMiddleware;
