import React from 'react';
import { connect, useDispatch } from 'react-redux';
import { Redirect } from 'react-router';
import {
    USERNAME_ENDRE,
    PASSWORD_ENDRE,
    LOGIN_HENT,
} from '../actiontypes';
import LoginMessage from './LoginMessage';

function Login(props) {
    const { username, password, loginresultat } = props;
    const dispatch = useDispatch();
    if (loginresultat.suksess) {
        const originalRequestUrl = loginresultat.originalRequestUrl || '/handlereg/';
        return (<Redirect to={originalRequestUrl} />);
    }

    return (
        <div className="Login">
            <header>
                <div className="pb-2 mt-4 mb-2 border-bottom bg-light">
                    <h1>Handleregistrering login</h1>
                    <p id="messagebanner"></p>
                </div>
            </header>
            <div className="container">
                <LoginMessage/>
                <form onSubmit={e => { e.preventDefault(); }}>
                    <div className="form-group row">
                        <label htmlFor="username" className="col-form-label col-3 mr-2">Username:</label>
                        <div className="col-8">
                            <input id="username" className="form-control" type="text" name="username" value={username} onChange={e => dispatch(USERNAME_ENDRE(e.target.value))} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="password" className="col-form-label col-3 mr-2">Password:</label>
                        <div className="col-8">
                            <input id="password" className="form-control" type="password" name="password" value={password} onChange={e => dispatch(PASSWORD_ENDRE(e.target.value))}/>
                        </div>
                    </div>
                    <div className="form-group row">
                        <div className="offset-xs-3 col-xs-9">
                            <input className="btn btn-primary" type="submit" value="Login" onClick={() => dispatch(LOGIN_HENT({ username, password }))}/>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    );
}

function mapStateToProps(state) {
    const { username, password, loginresultat } = state;
    return {
        username,
        password,
        loginresultat,
    };
}

export default connect(mapStateToProps)(Login);
