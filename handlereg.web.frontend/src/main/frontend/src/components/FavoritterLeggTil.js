import React from 'react';
import { connect, useDispatch } from 'react-redux';
import { Container } from './bootstrap/Container';
import { StyledLinkLeft } from './bootstrap/StyledLinkLeft';
import {
    VELG_FAVORITTBUTIKK,
    LEGG_TIL_FAVORITT,
} from '../actiontypes';

function FavoritterLeggTil(props) {
    const { butikker, favoritter, favorittbutikk } = props;
    const dispatch = useDispatch();
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
                    { favoritter.map(f => <div key={'favoritt_' + f.favouriteid} className="btn btn-primary w-75 m-1 left-align-cell">{f.store.butikknavn}</div>) }
                </div>
                <form onSubmit={ e => { e.preventDefault(); }}>
                    <select value={favorittbutikk} onChange={e => dispatch(VELG_FAVORITTBUTIKK(e.target.value))}>
                        { ledigeButikker.map(b => <option key={'butikk_' + b.storeId.toString()} value={b.storeId}>{b.butikknavn}</option>) }
                    </select>
                    <div>
                        <button className="btn btn-primary" disabled={ingenButikkValgt} onClick={() => dispatch(LEGG_TIL_FAVORITT())}>Legg til favoritt</button>
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

export default connect(mapStateToProps)(FavoritterLeggTil);
