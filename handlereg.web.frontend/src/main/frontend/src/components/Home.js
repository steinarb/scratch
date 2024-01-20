import React from 'react';
import { Navigate } from 'react-router-dom';
import { useSelector, useDispatch } from 'react-redux';
import {
    BELOP_ENDRE,
    HOME_BUTIKKNAVN_ENDRE,
    DATO_ENDRE,
    NYHANDLING_REGISTRER,
} from '../actiontypes';
import { Container } from './bootstrap/Container';
import { StyledLinkRight } from './bootstrap/StyledLinkRight';
import Kvittering from './Kvittering';


export default function Home() {
    const loginresultat = useSelector(state => state.loginresultat);
    if (!loginresultat.authorized) {
        return <Navigate to="/handlereg/unauthorized" />;
    }

    const oversikt = useSelector(state => state.oversikt);
    const username = oversikt.brukernavn;
    const handlinger = useSelector(state => state.handlinger);
    const butikker = useSelector(state => state.butikker);
    const storeId = useSelector(state => state.storeId);
    const butikknavn = useSelector(state => state.butikknavn);
    const handletidspunkt = useSelector(state => state.handletidspunkt);
    const handledato = handletidspunkt.split('T')[0];
    const belop = useSelector(state => state.belop).toString();
    const dispatch = useDispatch();

    return (
        <div>
            <nav>
                <a href="../.."><span title="chevron left" aria-hidden="true"></span>&nbsp;Gå hjem!</a>
                <h1>Matregnskap</h1>
                <StyledLinkRight to="/handlereg/hurtigregistrering">Hurtig</StyledLinkRight>
            </nav>
            <Container>
                <p>Hei {oversikt.fornavn}!</p>
                <p>Dine 5 siste innkjøp, er:</p>
                <div>
                    <table>
                        <thead>
                            <tr>
                                <th>Dato</th>
                                <th>Beløp</th>
                                <th>Butikk</th>
                            </tr>
                        </thead>
                        <tbody>
                            {handlinger.map((handling) =>
                                            <tr key={handling.transactionId}>
                                                <td>{new Date(handling.handletidspunkt).toISOString().split('T')[0]}</td>
                                                <td>{handling.belop}</td>
                                                <td>{handling.butikk}</td>
                                            </tr>
                                           )}
                        </tbody>
                    </table>
                </div>
                <form onSubmit={ e => { e.preventDefault(); }}>
                    <div>
                        <label htmlFor="amount">Nytt beløp</label>
                        <div>
                            <input id="amount" type="number" pattern="\d+" value={belop} onChange={e => dispatch(BELOP_ENDRE(e.target.value))} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="jobtype">Velg butikk</label>
                        <div>
                            <input
                                list="butikker"
                                id="valgt-butikk"
                                name="valgt-butikk"
                                value={butikknavn}
                                onChange={e => dispatch(HOME_BUTIKKNAVN_ENDRE(e.target.value))}/>
                            <datalist id="butikker">
                                <option key="-1" value="" />
                                {butikker.map(butikk => <option key={butikk.storeId} value={butikk.butikknavn}/>)}
                            </datalist>
                        </div>
                    </div>
                    <div>
                        <label htmlFor="date">Dato</label>
                        <div>
                            <input
                                id="date"

                                type="date"
                                value={handledato}
                                onChange={e => dispatch(DATO_ENDRE(e.target.value))}
                            />
                        </div>
                    </div>
                    <div>
                        <div/>
                        <div>
                            <button disabled={belop <= 0} onClick={() => dispatch(NYHANDLING_REGISTRER({ storeId, belop, handletidspunkt, username }))}>Registrer handling</button>
                        </div>
                    </div>
                </form>
                <Kvittering/>
            </Container>
            <Container>
                <StyledLinkRight to="/handlereg/statistikk">Statistikk</StyledLinkRight>
                <StyledLinkRight to="/handlereg/nybutikk">Ny butikk</StyledLinkRight>
                <StyledLinkRight to="/handlereg/endrebutikk">Endre butikk</StyledLinkRight>
                <StyledLinkRight to="/handlereg/favoritter">Favoritter</StyledLinkRight>
            </Container>
        </div>
    );
}
