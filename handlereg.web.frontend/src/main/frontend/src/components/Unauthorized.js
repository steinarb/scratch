import React from 'react';
import { Navigate } from 'react-router-dom';
import { useSelector, useDispatch } from 'react-redux';
import {
    LOGOUT_HENT,
} from '../actiontypes';
import { Container } from './bootstrap/Container';


export default function Unauthorized() {
    const loginresultat = useSelector(state => state.loginresultat);
    const dispatch = useDispatch();
    if (!loginresultat.suksess) {
        return <Navigate to="/handlereg/login" />;
    }

    return (
        <div>
            <nav>
                <a href="../.."><span title="chevron left" aria-hidden="true"></span>&nbsp;Gå hjem!</a>
                <h1>Ingen tilgang</h1>
                <div></div>
            </nav>
            <Container>
                <p>Hei {loginresultat.brukernavn}! Du har ikke tilgang til denne applikasjonen</p>
                <p>Klikk &quot;Gå hjem&quot; for å navigere ut av applikasjonen, eller logg ut for å logge inn med en bruker som har tilgang</p>
                <form onSubmit={ e => { e.preventDefault(); }}>
                    <div>
                        <div/>
                        <div>
                            <button onClick={() => dispatch(LOGOUT_HENT())}>Logg ut</button>
                        </div>
                    </div>
                </form>
            </Container>
        </div>
    );
}
