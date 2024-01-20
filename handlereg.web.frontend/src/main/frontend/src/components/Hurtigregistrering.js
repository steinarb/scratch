import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { Container } from './bootstrap/Container';
import { StyledLinkLeft } from './bootstrap/StyledLinkLeft';
import Kvittering from './Kvittering';
import {
    BELOP_ENDRE,
    NYHANDLING_REGISTRER,
} from '../actiontypes';

export default function Hurtigregistrering() {
    const username = useSelector(state => state.loginresultat.brukernavn);
    const favoritter = useSelector(state => state.favoritter);
    const handletidspunkt = useSelector(state => state.handletidspunkt);
    const belop = useSelector(state => state.belop).toString();
    const dispatch = useDispatch();

    return (
        <div>
            <nav>
                <StyledLinkLeft to="/handlereg">Opp til matregnskap</StyledLinkLeft>
                <h1>Hurtigregistrering</h1>
                <div></div>
            </nav>
            <Container>
                <form onSubmit={ e => { e.preventDefault(); }}>
                    <div>
                        <label htmlFor="amount">Nytt beløp</label>
                        <div>
                            <input id="amount" type="number" pattern="\d+" value={belop} onChange={e => dispatch(BELOP_ENDRE(e.target.value))} />
                        </div>
                    </div>
                    <Kvittering/>
                    { favoritter.map(f => <button key={'favoritt_' + f.favouriteid.toString()} disabled={belop <= 0} onClick={() => dispatch(NYHANDLING_REGISTRER({ storeId: f.store.storeId, belop, handletidspunkt, username }))}>{f.store.butikknavn}</button>) }
                </form>
            </Container>
        </div>
    );
}
