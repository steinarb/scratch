import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { stringify } from 'qs';
import { LOGOUT_REQUEST } from '../reduxactions';

export default function LoginLogoutButton() {
    const text = useSelector(state => state.displayTexts);
    const loggedIn = useSelector(state => state.loggedIn);
    const username = useSelector(state => state.username);
    const canLogin = useSelector(state => state.canLogin);
    const routerBasename = useSelector(state => state.router.basename);
    const dispatch = useDispatch();

    if (!canLogin) {
        return null;
    }

    if (loggedIn) {
        return (<span className="{props.styleName} alert" role="alert">
                    {text.loggedinas} {username} <span className="alert-link" onClick={() => dispatch(LOGOUT_REQUEST())}>{text.logout}</span>
                </span>);
    }

    const originalUri = window.location.href;
    const basename = routerBasename == '/' ? '' : routerBasename;
    const loginpath = basename + '/auth/login?' + stringify({ originalUri });
    return(<span className="alert" role="alert">{text.notloggedin} <a className="alert-link" href={loginpath}>{text.login}</a></span>);
}
