import React from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router-dom';
import { Header } from './bootstrap/Header';
import { Container } from './bootstrap/Container';
import { StyledLinkLeft } from './bootstrap/StyledLinkLeft';
import { StyledLinkRight } from './bootstrap/StyledLinkRight';
import {
    BELOP_ENDRE,
    NYHANDLING_REGISTRER,
} from '../actiontypes';

function Hurtigregistrering(props) {
    const {
        brukernavn,
        favoritter,
        nyhandling,
        endreBelop,
        onRegistrerHandling,
    } = props;

    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <StyledLinkLeft to="/handlereg">Opp til matregnskap</StyledLinkLeft>
                <h1>Hurtigregistrering</h1>
                <div className="col-sm-2"></div>
            </nav>
            <Container>
                <form onSubmit={ e => { e.preventDefault(); }}>
                    <div className="form-group row">
                        <label htmlFor="amount" className="col-form-label col-5">Nytt beløp</label>
                        <div className="col-7">
                            <input id="amount" className="form-control" type="text" value={nyhandling.belop} onChange={e => endreBelop(e.target.value)} />
                        </div>
                    </div>
                    { favoritter.map(f => <button className="btn btn-primary w-75 m-1 left-align-cell" onClick={() => onRegistrerHandling(nyhandling, f, brukernavn)}>{f.store.butikknavn}</button>) }
                </form>
            </Container>
        </div>
    );
}

function mapStateToProps(state) {
    const { brukernavn, favoritter, nyhandling } = state;
    return {
        brukernavn,
        favoritter,
        nyhandling,
    };
}


function mapDispatchToProps(dispatch) {
    return {
        endreBelop: (belop) => dispatch(BELOP_ENDRE(belop)),
        onRegistrerHandling: (handling, favoritt, username) => dispatch(NYHANDLING_REGISTRER({ ...handling, storeId: favoritt.store.storeId, username })),
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(Hurtigregistrering);
