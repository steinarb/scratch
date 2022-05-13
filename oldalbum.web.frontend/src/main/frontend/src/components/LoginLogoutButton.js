import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { NavLink } from 'react-router-dom';
import { stringify } from 'qs';
import { LOGOUT_REQUEST } from '../reduxactions';

export default function LoginLogoutButton(props) {
    const { item } = props;
    const loggedIn = useSelector(state => state.loggedIn);
    const username = useSelector(state => state.username);
    const canLogin = useSelector(state => state.canLogin);
    const dispatch = useDispatch();

    if (!canLogin) {
        return null;
    }

    if (loggedIn) {
        return (<span className="{props.styleName} alert alert-primary" role="alert">
                    Logged in as {username} <span className="alert-link" onClick={() => dispatch(LOGOUT_REQUEST())()}>Logout</span>
                </span>);
    }

    const returnpath = item.path || '/';
    const loginpath = '/login?' + stringify({ returnpath });
    return(<span className="alert alert-primary" role="alert">Not logged in <NavLink className="alert-link" to={loginpath}>Log in</NavLink></span>);
}
