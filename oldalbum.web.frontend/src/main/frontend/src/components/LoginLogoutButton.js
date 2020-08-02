import React from 'react';
import { connect } from 'react-redux';
import { NavLink } from 'react-router-dom';
import { LOGOUT_REQUEST } from '../reduxactions';

function LoginLogoutButton(props) {
    const { loginresult, onLogout } = props;
    if (loginresult.success) {
        return (<div>Logged in as {loginresult.username} <button className="btn btn-default" onClick={() => onLogout()}>Logout</button></div>);
    }

    return(<div>Not logged in <NavLink className="btn btn-default" to='/oldalbum/login'>Log in</NavLink></div>);
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
