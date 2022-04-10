import React from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { userIsNotLoggedIn } from '../common/login';
import {
    SELECT_JOB_TYPE,
    MODIFY_TRANSACTION_TYPE_NAME,
    MODIFY_JOB_AMOUNT,
    MODIFY_JOBTYPE_REQUEST,
    LOGOUT_REQUEST,
} from '../actiontypes';
import Locale from './Locale';
import JobtypesBox from './JobtypesBox';

function AdminJobtypesModify(props) {
    const {
        text,
        jobtypes,
        transactionTypeId,
        transactionAmount,
        transactionTypeName,
        onTransactionTypeIdChange,
        onNameFieldChange,
        onAmountFieldChange,
        onSaveUpdatedJobType,
        onLogout
    } = props;

    if (userIsNotLoggedIn(props)) {
        return <Redirect to="/ukelonn/login" />;
    }

    return (
        <div>
            <nav>
                <Link to="/ukelonn/admin/jobtypes">
                    &lt;-
                    &nbsp;
                    {text.administrateJobsAndJobTypes}
                </Link>
                <h1>{text.modifyJobTypes}</h1>
                <Locale />
            </nav>
            <div>
                <form onSubmit={ e => { e.preventDefault(); }}>
                    <div>
                        <label htmlFor="jobtype">{text.chooseJobType}</label>
                        <div>
                            <JobtypesBox id="jobtype" jobtypes={jobtypes} value={transactionTypeId} onJobtypeFieldChange={onTransactionTypeIdChange} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="amount">{text.modifyNameOfJobType}</label>
                        <div>
                            <input id="name" type="text" value={transactionTypeName} onChange={e => onNameFieldChange(e.target.value)} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="amount">{text.modifyAmountOfJobType}</label>
                        <div>
                            <input id="amount" type="text" value={transactionAmount} onChange={e => onAmountFieldChange(e.target.value)} />
                        </div>
                    </div>
                    <div>
                        <div/>
                        <div>
                            <button onClick={() => onSaveUpdatedJobType({ id: transactionTypeId, transactionTypeName, transactionAmount })}>{text.saveChangesToJobType}</button>
                        </div>
                    </div>
            </form>
            </div>
            <br/>
            <button onClick={() => onLogout()}>{text.logout}</button>
            <br/>
            <a href="../../../..">{text.returnToTop}</a>
        </div>
    );
}

function mapStateToProps(state) {
    return {
        text: state.displayTexts,
        haveReceivedResponseFromLogin: state.haveReceivedResponseFromLogin,
        loginResponse: state.loginResponse,
        jobtypes: state.jobtypes,
        transactionTypeId: state.transactionTypeId,
        transactionAmount: state.transactionAmount,
        transactionTypeName: state.transactionTypeName,
    };
}

function mapDispatchToProps(dispatch) {
    return {
        onTransactionTypeIdChange: value => dispatch(SELECT_JOB_TYPE(parseInt(value))),
        onNameFieldChange: value => dispatch(MODIFY_TRANSACTION_TYPE_NAME(value)),
        onAmountFieldChange: value => dispatch(MODIFY_JOB_AMOUNT(value)),
        onSaveUpdatedJobType: jobtype => dispatch(MODIFY_JOBTYPE_REQUEST(jobtype)),
        onLogout: () => dispatch(LOGOUT_REQUEST()),
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(AdminJobtypesModify);
