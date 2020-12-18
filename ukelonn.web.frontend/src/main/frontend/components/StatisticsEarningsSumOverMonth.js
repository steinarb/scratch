import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { stringify } from 'qs';
import { findUsernameFromAccountOrQueryParameter } from '../common/account';
import { userIsNotLoggedIn } from '../common/login';
import {
    LOGOUT_REQUEST,
} from '../actiontypes';
import Locale from './Locale';

function StatisticsEarningsSumOverMonth(props) {
    if (userIsNotLoggedIn(props)) {
        return <Redirect to="/ukelonn/login" />;
    }

    let { text, earningsSumOverMonth, onLogout } = props;
    const username = findUsernameFromAccountOrQueryParameter(props);
    const statistics = '/ukelonn/statistics?' + stringify({ username });

    return (
        <div>
            <Link to={statistics}>
                &lt;-
                &nbsp;
                {text.backToStatistics}
            </Link>
            <header>
                <div>
                    <h1>{text.sumAmountEarnedPerMonthAndYear}</h1>
                </div>
                <div>
                    <Locale />
                </div>
            </header>
            <div>
                <table className="table table-bordered">
                    <thead>
                        <tr>
                            <td>{text.year}</td>
                            <td>{text.month}</td>
                            <td>{text.totalAmountEarned}</td>
                        </tr>
                    </thead>
                    <tbody>
                        {earningsSumOverMonth.map((sumOverMonth) =>
                                                  <tr key={''.concat(sumOverMonth.year, sumOverMonth.month)}>
                                                      <td>{sumOverMonth.year}</td>
                                                      <td>{sumOverMonth.month}</td>
                                                      <td>{sumOverMonth.sum}</td>
                                                  </tr>
                                                 )}
                    </tbody>
                </table>
            </div>
            <br/>
            <button onClick={() => onLogout()}>{text.logout}</button>
            <br/>
            <a href="../../..">{text.returnToTop}</a>
        </div>
    );
}

function mapStateToProps(state) {
    return {
        text: state.displayTexts,
        haveReceivedResponseFromLogin: state.haveReceivedResponseFromLogin,
        loginResponse: state.loginResponse,
        account: state.account,
        earningsSumOverMonth: state.earningsSumOverMonth,
    };
}

function mapDispatchToProps(dispatch) {
    return {
        onLogout: () => dispatch(LOGOUT_REQUEST()),
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(StatisticsEarningsSumOverMonth);
