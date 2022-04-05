import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { Container } from './bootstrap/Container';
import { StyledLinkLeft } from './bootstrap/StyledLinkLeft';
import {
    VELG_FAVORITTBUTIKK,
    LEGG_TIL_FAVORITT,
} from '../actiontypes';

export default function FavoritterLeggTil() {
    const butikker = useSelector(state => state.butikker);
    const favoritter = useSelector(state => state.favoritter);
    const favorittbutikk = useSelector(state => state.favorittbutikk);
    const ledigeButikker = butikker.filter(butikk => !favoritter.find(fav => fav.store.storeId === butikk.storeId));
    const ingenButikkValgt = favorittbutikk === -1;
    const dispatch = useDispatch();

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
                    <select value={favorittbutikk} onChange={e => dispatch(VELG_FAVORITTBUTIKK(parseInt(e.target.value)))}>
                        <option key="butikk_-1" value="-1" />
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
