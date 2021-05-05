import React from 'react';
import { connect } from 'react-redux';
import { Container } from './bootstrap/Container';
import { StyledLinkLeft } from './bootstrap/StyledLinkLeft';
import {
    SLETT_FAVORITT,
} from '../actiontypes';

function FavoritterSlett(props) {
    const { favoritter, onSlettFavoritt } = props;

    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <StyledLinkLeft to="/handlereg/favoritter">Tilbake</StyledLinkLeft>
                <h1>Slett butikk(er) fra favoritt-lista</h1>
                <div className="col-sm-2"></div>
            </nav>
            <Container>
                { favoritter.map(f => <button key={'favoritt_' + f.favouriteid} className="btn btn-primary w-75 m-1 left-align-cell" onClick={() => onSlettFavoritt(f)}>{f.store.butikknavn}</button>) }
            </Container>
        </div>
    );
}

function mapStateToProps(state) {
    const favoritter = state.favoritter;
    return {
        favoritter,
    };
}

function mapDispatchToProps(dispatch) {
    return {
        onSlettFavoritt: favoritt => dispatch(SLETT_FAVORITT(favoritt))
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(FavoritterSlett);
