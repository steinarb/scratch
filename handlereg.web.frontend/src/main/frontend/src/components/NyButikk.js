import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { Container } from './bootstrap/Container';
import { StyledLinkLeft } from './bootstrap/StyledLinkLeft';
import {
    BUTIKKNAVN_ENDRE,
    NYBUTIKK_REGISTRER,
} from '../actiontypes';

export default function NyButikk() {
    const butikknavn = useSelector(state => state.butikknavn);
    const dispatch = useDispatch();

    return (
        <div>
            <nav>
                <StyledLinkLeft to="/handlereg">Opp til matregnskap</StyledLinkLeft>
                <h1>Ny butikk</h1>
                <div></div>
            </nav>
            <Container>
                <form onSubmit={ e => { e.preventDefault(); }}>
                    <div>
                        <label htmlFor="amount">Ny butikk</label>
                        <div>
                            <input id="amount" type="text" value={butikknavn} onChange={e => dispatch(BUTIKKNAVN_ENDRE(e.target.value))} />
                        </div>
                    </div>
                    <div>
                        <div/>
                        <div>
                            <button onClick={() => dispatch(NYBUTIKK_REGISTRER(butikknavn))}>Legg til butikk</button>
                        </div>
                    </div>
                </form>
            </Container>
        </div>
    );
}
