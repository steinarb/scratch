import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { userIsNotLoggedIn } from '../common/login';
import {
    LOGOUT_REQUEST,
} from '../actiontypes';
import Locale from './Locale';

function AdminBonuses(props) {
    if (userIsNotLoggedIn(props)) {
        return <Redirect to="/ukelonn/login" />;
    }

    let { text, onLogout } = props;

    return (
        <div>
            <Link className="btn btn-block btn-primary mb-0 left-align-cell" to="/ukelonn/">
                <span className="oi oi-chevron-left" title="chevron left" aria-hidden="true"></span>
                &nbsp;
                {text.registerPayment}
            </Link>
            <header>
                <div className="pb-2 mt-0 mb-2 border-bottom bg-light">
                    <h1>{text.administrateBonuses}</h1>
                </div>
                <div>
                    <Locale />
                </div>
            </header>
            <div>
                <Link className="btn btn-block btn-primary right-align-cell" to="/ukelonn/admin/bonuses/modify">
                    {text.modifyBonuses}
                    &nbsp;
                    <span className="oi oi-chevron-right" title="chevron right" aria-hidden="true"></span>
                </Link>
                <Link className="btn btn-block btn-primary right-align-cell" to="/ukelonn/admin/bonuses/create">
                    {text.createNewBonus}
                    &nbsp;
                    <span className="oi oi-chevron-right" title="chevron right" aria-hidden="true"></span>
                </Link>
                <Link className="btn btn-block btn-primary right-align-cell" to="/ukelonn/admin/bonuses/delete">
                    {text.deleteBonuses}
                    &nbsp;
                    <span className="oi oi-chevron-right" title="chevron right" aria-hidden="true"></span>
                </Link>
            </div>
            <br/>
            <br/>
            <button onClick={() => onLogout()}>{text.logout}</button>
            <br/>
            <a href="../../..">{text.returnToTop}</a>
        </div>
    );
};

function mapStateToProps(state) {
    return {
        text: state.displayTexts,
        haveReceivedResponseFromLogin: state.haveReceivedResponseFromLogin,
        loginResponse: state.loginResponse,
    };
}

function mapDispatchToProps(dispatch) {
    return {
        onLogout: () => dispatch(LOGOUT_REQUEST()),
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(AdminBonuses);
