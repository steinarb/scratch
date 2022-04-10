import React from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { userIsNotLoggedIn } from '../common/login';
import {
    MODIFY_TRANSACTION_TYPE_NAME,
    MODIFY_JOB_AMOUNT,
    CREATE_JOBTYPE_REQUEST,
    LOGOUT_REQUEST,
} from '../actiontypes';
import Locale from './Locale';

function AdminJobtypesCreate(props) {
    const {
        text,
        transactionTypeName,
        transactionAmount,
        onNameFieldChange,
        onAmountFieldChange,
        onSaveUpdatedJobType,
        onLogout,
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
                <h1>{text.createNewJobType}</h1>
                <Locale />
            </nav>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <div>
                    <div>
                        <label htmlFor="amount">{text.nameOfJobType}</label>
                        <div>
                            <input id="name" type="text" value={transactionTypeName} onChange={e => onNameFieldChange(e.target.value)} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="amount">{text.amountForJobType}</label>
                        <div>
                            <input id="amount" type="text" value={transactionAmount} onChange={e => onAmountFieldChange(e.target.value)} />
                        </div>
                    </div>
                    <div>
                        <div/>
                        <div>
                            <button onClick={() => onSaveUpdatedJobType({ transactionTypeName, transactionAmount })}>{text.createNewJobType}</button>
                        </div>
                    </div>
                </div>
            </form>
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
        transactionTypeName: state.transactionTypeName,
        transactionAmount: state.transactionAmount,
    };
}

function mapDispatchToProps(dispatch) {
    return {
        onNameFieldChange: transactionTypeName => dispatch(MODIFY_TRANSACTION_TYPE_NAME(transactionTypeName)),
        onAmountFieldChange: transactionAmount => dispatch(MODIFY_JOB_AMOUNT(transactionAmount)),
        onSaveUpdatedJobType: transactiontype => dispatch(CREATE_JOBTYPE_REQUEST(transactiontype)),
        onLogout: () => dispatch(LOGOUT_REQUEST()),
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(AdminJobtypesCreate);
