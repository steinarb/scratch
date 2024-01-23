import React from 'react';
import { useSelector } from 'react-redux';

export default function LoginMessage() {
    const loginresultat = useSelector(state => state.loginresultat);
    if (!loginresultat.feilmelding) {
        return null;
    }

    return (
        <div role='alert' className="bg-yellow-100 border border-red-500 rounded py-5 px-5 my-5 mx-5">
            {loginresultat.feilmelding}
        </div>
    );
}
