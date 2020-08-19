import React from 'react';
import { connect } from 'react-redux';
import { NavLink } from 'react-router-dom';
import { stringify } from 'qs';
import { LOGOUT_REQUEST } from '../reduxactions';

function LoginLogoutButton(props) {
    const { loginresult, item, onLogout } = props;
    if (loginresult.success) {
        return (<span className="{props.styleName} alert alert-primary" role="alert">Logged in as {loginresult.username} <span className="alert-link" onClick={() => onLogout()}>Logout</span></span>);
    }

    const returnpath = item.path || '/';
    const loginpath = '/login?' + stringify({ returnpath });
    return(<span className="alert alert-primary" role="alert">Not logged in <NavLink className="alert-link" to={loginpath}>Log in</NavLink></span>);
}

function mapStateToProps(state) {
    const login = state.login || {};
    const loginresult = login.loginresult || { success: false };
    return {
        loginresult,
    };
}

function mapDispatchToProps(dispatch) {
    return {
        onLogout: () => dispatch(LOGOUT_REQUEST()),
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(LoginLogoutButton);
