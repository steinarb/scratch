import React, { Component } from 'react';
import { connect } from 'react-redux';
import moment from 'moment';
import { Link } from 'react-router-dom';
import { Header } from './bootstrap/Header';
import { Container } from './bootstrap/Container';
import { StyledLinkLeft } from './bootstrap/StyledLinkLeft';
import { StyledLinkRight } from './bootstrap/StyledLinkRight';
import {
    VELG_FAVORITTBUTIKK,
    LEGG_TIL_FAVORITT,
} from '../actiontypes';

function FavoritterLeggTil(props) {
    const { butikker, favoritter, favorittbutikk, velgButikk, onLeggTilFavoritt } = props;
    const ledigeButikker = butikker.filter(butikk => !favoritter.find(fav => fav.store.storeId === butikk.storeId));
    const ingenButikkValgt = favorittbutikk === -1;

    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <StyledLinkLeft to="/handlereg/favoritter">Tilbake</StyledLinkLeft>
                <h1>Legg til favoritt-butikk</h1>
                <div className="col-sm-2"></div>
            </nav>
            <Container>
                <div>
                    { favoritter.map(f => <div className="btn btn-primary w-75 m-1 left-align-cell">{f.store.butikknavn}</div>) }
                </div>
                <form onSubmit={ e => { e.preventDefault(); }}>
                    <select value={favorittbutikk} onChange={e => velgButikk(e.target.value)}>
                        { ledigeButikker.map(b => <option key={b.storeId} value={b.storeId}>{b.butikknavn}</option>) }
                    </select>
                    <div>
                        <button className="btn btn-primary" disabled={ingenButikkValgt} onClick={onLeggTilFavoritt}>Legg til favoritt</button>
                    </div>
                </form>
            </Container>
        </div>
    );
}

function mapStateToProps(state) {
    const butikker = state.butikker;
    const favoritter = state.favoritter;
    const favorittbutikk = state.favorittbutikk;
    return {
        butikker,
        favoritter,
        favorittbutikk,
    };
}

function mapDispatchToProps(dispatch) {
    return {
        velgButikk: storeId => dispatch(VELG_FAVORITTBUTIKK(storeId)),
        onLeggTilFavoritt: () => dispatch(LEGG_TIL_FAVORITT())
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(FavoritterLeggTil);
