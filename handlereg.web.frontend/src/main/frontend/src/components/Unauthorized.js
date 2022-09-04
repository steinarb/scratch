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
            <nav className="navbar navbar-light bg-light">
                <a className="btn btn-primary left-align-cell" href="../.."><span className="oi oi-chevron-left" title="chevron left" aria-hidden="true"></span>&nbsp;Gå hjem!</a>
                <h1>Ingen tilgang</h1>
                <div className="col-sm-2"></div>
            </nav>
            <Container>
                <p>Hei {loginresultat.brukernavn}! Du har ikke tilgang til denne applikasjonen</p>
                <p>Klikk &quot;Gå hjem&quot; for å navigere ut av applikasjonen, eller logg ut for å logge inn med en bruker som har tilgang</p>
                <form onSubmit={ e => { e.preventDefault(); }}>
                    <div className="form-group row">
                        <div className="col-5"/>
                        <div className="col-7">
                            <button className="btn btn-primary" onClick={() => dispatch(LOGOUT_HENT())}>Logg ut</button>
                        </div>
                    </div>
                </form>
            </Container>
        </div>
    );
}