import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { Container } from './bootstrap/Container';
import { StyledLinkLeft } from './bootstrap/StyledLinkLeft';
import {
    VELG_BUTIKK,
    BUTIKKNAVN_ENDRE,
    BUTIKK_LAGRE,
} from '../actiontypes';

export default function EndreButikk() {
    const valgtButikk = useSelector(state => state.valgtButikk);
    const butikknavn = useSelector(state => state.butikknavn);
    const butikker = useSelector(state => state.butikker);
    const dispatch = useDispatch();

    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <StyledLinkLeft to="/handlereg">Opp til matregnskap</StyledLinkLeft>
                <h1>Endre butikk</h1>
                <div className="col-sm-2"></div>
            </nav>
            <Container>
                <form onSubmit={ e => { e.preventDefault(); }}>
                    <select size="10" value={valgtButikk} onChange={e => dispatch(VELG_BUTIKK(e.target.value))}>
                        { butikker.map((b, indeks) => <option key={'butikk_' + b.storeId.toString()} value={indeks}>{b.butikknavn}</option>) }
                    </select>
                    <div className="form-group row">
                        <label htmlFor="amount" className="col-form-label col-5">Butikknavn</label>
                        <div className="col-7">
                            <input id="amount" className="form-control" type="text" value={butikknavn} onChange={e => dispatch(BUTIKKNAVN_ENDRE(e.target.value))} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <div className="col-5"/>
                        <div className="col-7">
                            <button className="btn btn-primary" onClick={() => dispatch(BUTIKK_LAGRE(butikknavn))}>Lagre endret butikk</button>
                        </div>
                    </div>
                </form>
            </Container>
        </div>
    );
}
