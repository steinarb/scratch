import React from 'react';
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

function StatisticsEarningsSumOverYear(props) {
    if (userIsNotLoggedIn(props)) {
        return <Redirect to="/ukelonn/login" />;
    }

    let { text, earningsSumOverYear, onLogout } = props;

    const username = findUsernameFromAccountOrQueryParameter(props);
    const statistics = '/ukelonn/statistics?' + stringify({ username });

    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <Link className="btn btn-primary" to={statistics}>
                    <span className="oi oi-chevron-left" title="chevron left" aria-hidden="true"></span>
                    &nbsp;
                    {text.backToStatistics}
                </Link>
                <h1>{text.sumAmountEarnedPerYear}</h1>
                <Locale />
            </nav>
            <div className="table-responsive table-sm table-striped">
                <table className="table">
                    <thead>
                        <tr>
                            <th>{text.year}</th>
                            <th>{text.totalEarnings}</th>
                        </tr>
                    </thead>
                    <tbody>
                        {earningsSumOverYear.map((sumOverYear) =>
                            <tr key={sumOverYear.year}>
                                <td>{sumOverYear.year}</td>
                                <td>{sumOverYear.sum}</td>
                            </tr>
                        )}
                    </tbody>
                </table>
            </div>
            <br/>
            <button className="btn btn-default" onClick={() => onLogout()}>{text.returnToTop}</button>
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
        earningsSumOverYear: state.earningsSumOverYear,
    };
}

function mapDispatchToProps(dispatch) {
    return {
        onLogout: () => dispatch(LOGOUT_REQUEST()),
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(StatisticsEarningsSumOverYear);
