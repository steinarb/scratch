import React from 'react';
import { createRoot } from 'react-dom/client';
import axios from 'axios';
import './index.css';
import App from './App';
import * as serviceWorker from './serviceWorker';
import { configureStore } from '@reduxjs/toolkit';
import createSagaMiddleware from 'redux-saga';
import { Provider } from 'react-redux';
import { createReduxHistoryContext } from "redux-first-history";
import { createBrowserHistory } from 'history';
import createRootReducer from './reducers';
import rootSaga from './sagas';
import {
    SET_BASENAME,
    LOGIN_CHECK_REQUEST,
    ALLROUTES_REQUEST,
} from './reduxactions';

const baseUrl = Array.from(document.scripts).map(s => s.src).filter(src => src.includes('bundle.js'))[0].replace('/bundle.js', '');
const basename = new URL(baseUrl).pathname;
axios.defaults.baseURL = baseUrl;
const sagaMiddleware = createSagaMiddleware();
const {
  createReduxHistory,
  routerMiddleware,
  routerReducer
} = createReduxHistoryContext({ history: createBrowserHistory() });
const store = configureStore({
    reducer: createRootReducer(routerReducer),
    middleware: [
        sagaMiddleware,
        routerMiddleware,
    ],
});
sagaMiddleware.run(rootSaga);
const history = createReduxHistory(store);

// Initial actions to fetch data
store.dispatch(SET_BASENAME(basename));
store.dispatch(LOGIN_CHECK_REQUEST());
store.dispatch(ALLROUTES_REQUEST());

const container = document.getElementById('root');
const root = createRoot(container);

root.render(
    <Provider store={store}>
        <App history={history} basename={basename} />
    </Provider>,
);

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: http://bit.ly/CRA-PWA
serviceWorker.unregister();
