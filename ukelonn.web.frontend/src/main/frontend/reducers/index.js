import { combineReducers } from 'redux';
import { connectRouter } from 'connected-react-router';
import username from './usernameReducer';
import password from './passwordReducer';
import notificationAvailable from './notificationAvailableReducer';
import notificationMessage from './notificationMessageReducer';
import account from './accountReducer';
import accountId from './accountIdReducer';
import accountUsername from './accountUsernameReducer';
import accountFirstname from './accountFirstnameReducer';
import accountLastname from './accountLastnameReducer';
import accountBalance from './accountBalanceReducer';
import accountFullname from './accountFullnameReducer';
import payment from './paymentReducer';
import jobs from './jobsReducer';
import payments from './paymentsReducer';
import jobtypes from './jobtypesReducer';
import haveReceivedResponseFromLogin from './haveReceivedResponseFromLoginReducer';
import loginResponse from './loginResponseReducer';
import performedjob from './performedjobReducer';
import transactionId from './transactionIdReducer';
import transactionTypeId from './transactionTypeIdReducer';
import transactionTypeName from './transactionTypeNameReducer';
import transactionAmount from './transactionAmountReducer';
import transactionDate from './transactionDateReducer';
import selectedjob from './selectedjobReducer';
import accounts from './accountsReducer';
import paymenttypes from './paymenttypesReducer';
import users from './usersReducer';
import usernames from './usernamesReducer';
import user from './userReducer';
import passwords from './passwordsReducer';
import userIsAdministrator from './userIsAdministratorReducer';
import activebonuses from './activebonusesReducer';
import allbonuses from './allbonusesReducer';
import bonus from './bonusReducer';
import earningsSumOverYear from './earningsSumOverYearReducer';
import earningsSumOverMonth from './earningsSumOverMonthReducer';
import locale from './localeReducer';
import availableLocales from './availableLocalesReducer';
import displayTexts from './displayTextsReducer';

export default (history) => combineReducers({
    router: connectRouter(history),
    locale,
    availableLocales,
    displayTexts,
    username,
    password,
    notificationAvailable,
    notificationMessage,
    account,
    accountId,
    accountUsername,
    accountFirstname,
    accountLastname,
    accountBalance,
    accountFullname,
    payment,
    jobs,
    payments,
    jobtypes,
    haveReceivedResponseFromLogin,
    loginResponse,
    performedjob,
    transactionId,
    transactionTypeId,
    transactionTypeName,
    transactionAmount,
    transactionDate,
    selectedjob,
    accounts,
    paymenttypes,
    users,
    usernames,
    user,
    passwords,
    userIsAdministrator,
    activebonuses,
    allbonuses,
    bonus,
    earningsSumOverYear,
    earningsSumOverMonth,
});
