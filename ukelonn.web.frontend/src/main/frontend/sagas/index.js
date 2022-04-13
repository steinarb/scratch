import { fork, all } from 'redux-saga/effects';
import { requestInitialLoginStateSaga, requestLoginSaga } from './loginSaga';
import { requestLogoutSaga } from './logoutSaga';
import locationSaga from './locationSaga';
import accountSaga from './accountSaga';
import { requestJobtypeListSaga } from './jobtypelistSaga';
import { requestRegisterJobSaga } from './registerjobSaga';
import jobSaga from './jobSaga';
import paymentSaga from './paymentSaga';
import { requestRecentJobsSaga } from './recentjobsSaga';
import { requestRecentPaymentsSaga } from  './recentpaymentsSaga';
import { requestAccountsSaga } from './accountsSaga';
import paymenttypesSaga from './paymenttypesSaga';
import registerPaymentSaga from './registerpaymentSaga';
import { requestModifyJobtypeSaga } from './modifyjobtypeSaga';
import createPaymenttypeSaga from './createpaymenttypeSaga';
import createJobtypeSaga from './createjobtypeSaga';
import { requestDeleteJobsSaga } from './deletejobsSaga';
import { requestUpdateJobSaga } from './updatejobSaga';
import { requestModifyPaymenttypeSaga } from './modifypaymenttypeSaga';
import usersSaga from './usersSaga';
import modifyUserSaga from './modifyUserSaga';
import { requestCreateUserSaga } from './createuserSaga';
import adminstatusSaga from './adminstatusSaga';
import changeadminstatusSaga from './changeadminstatusSaga';
import { requestActivebonusesSaga } from './activebonusesSaga';
import { requestAllbonusesSaga } from './allbonusesSaga';
import modifybonusSaga from './modifybonusSaga';
import createbonusSaga from './createbonusSaga';
import deletebonusSaga from './deletebonusSaga';
import { requestChangePasswordSaga } from './modifyuserpasswordSaga';
import { startNotificationListening } from './notificationSaga';
import earningsSumOverYearSaga from './earningsSumOverYearSaga';
import earningsSumOverMonthSaga from './earningsSumOverMonthSaga';
import defaultLocaleSaga from './defaultLocaleSaga';
import localeSaga from './localeSaga';
import availableLocalesSaga from './availableLocalesSaga';
import displayTextsSaga from './displayTextsSaga';

export function* rootSaga() {
    yield all([
        fork(requestInitialLoginStateSaga),
        fork(requestLoginSaga),
        fork(requestLogoutSaga),
        fork(locationSaga),
        fork(accountSaga),
        fork(requestJobtypeListSaga),
        fork(requestRegisterJobSaga),
        fork(jobSaga),
        fork(paymentSaga),
        fork(requestRecentJobsSaga),
        fork(requestRecentPaymentsSaga),
        fork(requestAccountsSaga),
        fork(paymenttypesSaga),
        fork(registerPaymentSaga),
        fork(requestModifyJobtypeSaga),
        fork(createJobtypeSaga),
        fork(requestDeleteJobsSaga),
        fork(requestUpdateJobSaga),
        fork(requestModifyPaymenttypeSaga),
        fork(createPaymenttypeSaga),
        fork(usersSaga),
        fork(modifyUserSaga),
        fork(adminstatusSaga),
        fork(changeadminstatusSaga),
        fork(requestActivebonusesSaga),
        fork(requestAllbonusesSaga),
        fork(modifybonusSaga),
        fork(createbonusSaga),
        fork(deletebonusSaga),
        fork(requestCreateUserSaga),
        fork(requestChangePasswordSaga),
        fork(startNotificationListening),
        fork(earningsSumOverYearSaga),
        fork(earningsSumOverMonthSaga),
        fork(defaultLocaleSaga),
        fork(localeSaga),
        fork(availableLocalesSaga),
        fork(displayTextsSaga),
    ]);
}
