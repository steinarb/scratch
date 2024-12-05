import React, { useState } from 'react';
import { useSelector } from 'react-redux';
import { usePostLoginMutation } from '../api';
import LoginMessage from './LoginMessage';

export default function Login() {
    const [ username, setUsername ] = useState('');
    const [ password, setPassword ] = useState('');
    const basename = useSelector(state => state.basename);
    const loginresultat = useSelector(state => state.loginresultat);
    const [ postLogin ] = usePostLoginMutation();

    if (loginresultat.suksess) {
        const originalRequestUrl = findReloadUrl(basename, loginresultat.originalRequestUrl);
        location.href = originalRequestUrl;
    }

    const onLoginClicked = async () => {
        await postLogin({ username, password: btoa(password) })
    }

    return (
        <div>
            <header>
                <div className="flex items-center justify-between flex-wrap bg-slate-100 p-6 border rounded">
                    <h1 className="text-2xl font-bold">Handleregistrering login</h1>
                </div>
            </header>
            <div>
                <LoginMessage/>
                <form className="w-full max-w-lg mt-4 grid grid-flow-row auto-rows-max" onSubmit={e => { e.preventDefault(); }}>
                    <div className="columns-2 mb-2">
                        <label className="w-full ms-5 block uppercase text-gray-700 font-bold" htmlFor="username">Username:</label>
                        <input className="appearance-none w-full bg-gray-200 text-gray-700 border border-red-500 rounded py-3 px-4 focus:outline-none focus:bg-white" id="username" type="text" name="username" autoComplete="username" value={username} onChange={e => setUsername(e.target.value)} />
                    </div>
                    <div className="columns-2 mb-2">
                        <label className="w-full ms-5 block uppercase text-gray-700 font-bold" htmlFor="password">Password:</label>
                        <input className="appearance-none w-full bg-gray-200 text-gray-700 border border-red-500 rounded py-3 px-4 focus:outline-none focus:bg-white" id="password" type="password" name="password" autoComplete="current-password" value={password} onChange={e => setPassword(e.target.value)}/>
                    </div>
                    <div className="columns-2 mb-2">
                        <input className="w-full bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded" type="submit" value="Login" onClick={onLoginClicked}/>
                    </div>
                </form>
            </div>
        </div>
    );
}

function findReloadUrl(basename, originalRequestUrl) {
    // If originalRequestUrl is empty go to the top.
    // If originalRequest is /unauthorized go to the top and let shiro decide where to redirect to
    if (!originalRequestUrl || originalRequestUrl === '/unauthorized') {
        return basename + '/';
    }

    return basename + originalRequestUrl;
}
