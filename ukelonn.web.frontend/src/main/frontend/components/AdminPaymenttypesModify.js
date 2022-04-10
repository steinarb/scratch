import React from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { userIsNotLoggedIn } from '../common/login';
import {
    SELECT_PAYMENT_TYPE,
    MODIFY_TRANSACTION_TYPE_NAME,
    MODIFY_PAYMENT_AMOUNT,
    MODIFY_PAYMENTTYPE_REQUEST,
    LOGOUT_REQUEST,
} from '../actiontypes';
import Locale from './Locale';
import PaymenttypesBox from './PaymenttypesBox';

function AdminPaymenttypesModify(props) {
    const {
        text,
        paymenttypes,
        transactionTypeId,
        transactionTypeName,
        transactionAmount,
        onPaymenttypeFieldChange,
        onNameFieldChange,
        onAmountFieldChange,
        onSaveUpdatedPaymentType,
        onLogout,
    } = props;

    if (userIsNotLoggedIn(props)) {
        return <Redirect to="/ukelonn/login" />;
    }

    return (
        <div>
            <nav>
                <Link to="/ukelonn/admin/paymenttypes">
                    &lt;-
                    &nbsp;
                    {text.administratePaymenttypes}
                </Link>
                <h1>{text.modifyPaymenttypes}</h1>
                <Locale />
            </nav>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <div>
                    <div>
                        <label htmlFor="paymenttype">{text.choosePaymentType}</label>
                        <div>
                            <PaymenttypesBox id="paymenttype" value={transactionTypeId}  paymenttypes={paymenttypes} onPaymenttypeFieldChange={onPaymenttypeFieldChange} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="amount">{text.modifyPaymentTypeName}</label>
                        <div>
                            <input id="name" type="text" value={transactionTypeName} onChange={e => onNameFieldChange(e.target.value)} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="amount">{text.modifyPaymentTypeAmount}</label>
                        <div>
                            <input id="amount" type="text" value={transactionAmount} onChange={e => onAmountFieldChange(e.target.value)} />
                        </div>
                    </div>
                    <div>
                        <div/>
                        <div>
                            <button onClick={() => onSaveUpdatedPaymentType({ id: transactionTypeId, transactionTypeName, transactionAmount })}>{text.saveChangesToPaymentType}</button>
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
        haveReceivedResponseFromLogin: state.haveReceivedResponseFromLogin,
        loginResponse: state.loginResponse,
        paymenttypes: state.paymenttypes,
        transactionTypeId: state.transactionTypeId,
        transactionTypeName: state.transactionTypeName,
        transactionAmount: state.transactionAmount,
    };
}

function mapDispatchToProps(dispatch) {
    return {
        onPaymenttypeFieldChange: selectedValue => dispatch(SELECT_PAYMENT_TYPE(selectedValue)),
        onNameFieldChange: transactionTypeName => dispatch(MODIFY_TRANSACTION_TYPE_NAME(transactionTypeName)),
        onAmountFieldChange: transactionAmount => dispatch(MODIFY_PAYMENT_AMOUNT(transactionAmount)),
        onSaveUpdatedPaymentType: transactiontype => dispatch(MODIFY_PAYMENTTYPE_REQUEST(transactiontype)),
        onLogout: () => dispatch(LOGOUT_REQUEST()),
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(AdminPaymenttypesModify);
