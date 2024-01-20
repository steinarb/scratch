import React, { useState } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { Navigate } from 'react-router-dom';
import {
    LOGIN_HENT,
} from '../actiontypes';
import LoginMessage from './LoginMessage';

export default function Login() {
    const [ username, setUsername ] = useState('');
    const [ password, setPassword ] = useState('');
    const loginresultat = useSelector(state => state.loginresultat);
    const dispatch = useDispatch();

    if (loginresultat.suksess) {
        const originalRequestUrl = loginresultat.originalRequestUrl || '/handlereg/';
        return (<Navigate to={originalRequestUrl} />);
    }

    return (
        <div>
            <header>
                <div>
                    <h1>Handleregistrering login</h1>
                    <p id="messagebanner"></p>
                </div>
            </header>
            <div>
                <LoginMessage/>
                <form onSubmit={e => { e.preventDefault(); }}>
                    <div>
                        <label htmlFor="username">Username:</label>
                        <div>
                            <input id="username" type="text" name="username" autoComplete="username" value={username} onChange={e => setUsername(e.target.value)} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="password">Password:</label>
                        <div>
                            <input id="password" type="password" name="password" autoComplete="current-password" value={password} onChange={e => setPassword(e.target.value)}/>
                        </div>
                    </div>
                    <div>
                        <div>
                            <input type="submit" value="Login" onClick={() => dispatch(LOGIN_HENT({ username, password: btoa(password) }))}/>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    );
}
