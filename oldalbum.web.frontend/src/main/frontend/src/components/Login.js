import React, { useState } from 'react';
import { connect } from 'react-redux';
import { Redirect, NavLink } from 'react-router-dom';
import { parse } from 'qs';
import { LOGIN_REQUEST } from '../reduxactions';

function Login(props) {
    const {
        loginresult,
        location,
        onSendLogin,
    } = props;
    const [ username, setUsername ] = useState('');
    const [ password, setPassword ] = useState('');
    const errormessage = loginresult && loginresult.errormessage;
    const queryParams = (location.search && parse(location.search, { ignoreQueryPrefix: true })) || {};
    const { returnpath = '/' } = queryParams;
    if (loginresult.success) {
        return (<Redirect to={returnpath} />);
    }

    return (
        <div className="Login">
            <header>
                <div className="pb-2 mt-4 mb-2 border-bottom bg-light">
                    <h1>Album login</h1>
                    <p id="messagebanner"></p>
                </div>
            </header>
            <div className="container">
                <form onSubmit={e => { e.preventDefault(); }}>
                    <div className="form-group row">
                        <label htmlFor="username" className="col-form-label col-3 mr-2">Username:</label>
                        <div className="col-8">
                            <input id="username" className="form-control" type="text" name="username" value={username} onChange={e => setUsername(e.target.value)} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="password" className="col-form-label col-3 mr-2">Password:</label>
                        <div className="col-8">
                            <input id="password" className="form-control" type="password" name="password" value={password} onChange={e => setPassword(e.target.value)}/>

                        </div>
                    </div>
                    <div className="btn-group row right-align-cell">
                        <input className="btn btn-primary mx-2" type="submit" value="Login" onClick={() => onSendLogin(username, password)}/>
                        <NavLink className="btn btn-primary mx-2" to={returnpath}>Cancel</NavLink>
                    </div>
                </form>
                { errormessage && <div className="alert alert-warning">{errormessage}</div> }
            </div>
        </div>
    );
}

function mapStateToProps(state) {
    return {
        loginresult: state.loginresult,
    };
}

function mapDispatchToProps(dispatch) {
    return {
        onSendLogin: (username, password) => dispatch(LOGIN_REQUEST({ username, password })),
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(Login);
