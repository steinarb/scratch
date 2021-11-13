import { combineReducers } from 'redux';
import { connectRouter } from 'connected-react-router';
import username from './usernameReducer';
import password from './passwordReducer';
import loginresult from './loginresultReducer';
import accounts from './accountsReducer';
import counterIncrementStep from './counterIncrementStepReducer';
import counter from './counterReducer';
import locale from './localeReducer';
import availableLocales from './availableLocalesReducer';
import displayTexts from './displayTextsReducer';
import errors from './errorsReducer';

export default (history) => combineReducers({
    router: connectRouter(history),
    username,
    password,
    loginresult,
    accounts,
    counterIncrementStep,
    counter,
    locale,
    availableLocales,
    displayTexts,
    errors,
});
