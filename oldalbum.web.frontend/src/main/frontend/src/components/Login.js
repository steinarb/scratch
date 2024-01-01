import React, { useState } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { Navigate, NavLink, useSearchParams } from 'react-router-dom';
import { LOGIN_REQUEST } from '../reduxactions';

export default function Login() {
    const text = useSelector(state => state.displayTexts);
    const loggedIn = useSelector(state => state.loggedIn);
    const errormessage = useSelector(state => state.errormessage);
    const originalRequestUri = useSelector(state => state.originalRequestUri);
    const dispatch = useDispatch();
    const [ username, setUsername ] = useState('');
    const [ password, setPassword ] = useState('');
    const [searchParams] = useSearchParams();
    const returnpath = searchParams.get('returnpath') || '/';
    const loginButtonTitle = text.login || '';

    if (loggedIn) {
        if (originalRequestUri) {
            return (<Navigate to={originalRequestUri} />);
        }

        return (<Navigate to={returnpath} />);
    }

    return (
        <div className="Login">
            <header>
                <div className="pb-2 mt-4 mb-2 border-bottom bg-light">
                    <h1>{text.albumlogin}</h1>
                    <p id="messagebanner"></p>
                </div>
            </header>
            <div className="container">
                <form onSubmit={e => { e.preventDefault(); }}>
                    <div className="form-group row">
                        <label htmlFor="username" className="col-form-label col-3 mr-2">{text.username}:</label>
                        <div className="col-8">
                            <input id="username" className="form-control" type="text" name="username" autoComplete="username" value={username} onChange={e => setUsername(e.target.value)} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="password" className="col-form-label col-3 mr-2">{text.password}:</label>
                        <div className="col-8">
                            <input id="password" className="form-control" type="password" name="password" autoComplete="current-password" value={password} onChange={e => setPassword(e.target.value)}/>
                        </div>
                    </div>
                    <div className="btn-group row right-align-cell">
                        <input
                            className="btn btn-light mx-2"
                            type="submit"
                            value={loginButtonTitle}
                            onClick={() => dispatch(LOGIN_REQUEST({ username, password }))}/>
                        <NavLink className="btn btn-light mx-2" to={returnpath}>{text.cancel}</NavLink>
                    </div>
                </form>
                { errormessage && <div className="alert alert-warning">{errormessage}</div> }
            </div>
        </div>
    );
}
