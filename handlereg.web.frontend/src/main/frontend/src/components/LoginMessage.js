import React from 'react';
import { useSelector } from 'react-redux';

export default function LoginMessage() {
    const loginresultat = useSelector(state => state.loginresultat);
    if (!loginresultat.feilmelding) {
        return null;
    }

    return (
        <div role='alert'>
            {loginresultat.feilmelding}
        </div>
    );
}
