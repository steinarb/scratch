import React from 'react';
import { useSelector } from 'react-redux';
import { Container } from './bootstrap/Container';
import { StyledLinkLeft } from './bootstrap/StyledLinkLeft';

export default function StatistikkHandlingerbutikk() {
    const handlingerbutikk = useSelector(state => state.handlingerbutikk);

    return (
        <div>
            <nav>
                <StyledLinkLeft to="/handlereg/statistikk">Tilbake</StyledLinkLeft>
                <h1>Antall handlinger gjort i butikk</h1>
                <div></div>
            </nav>
            <Container>
                <div>
                    <table>
                        <thead>
                            <tr>
                                <td>Butikk</td>
                                <td>Antall handlinger</td>
                            </tr>
                        </thead>
                        <tbody>
                            {handlingerbutikk.map((hb) =>
                                                  <tr key={'butikk' + hb.butikk.storeId}>
                                                      <td>{hb.butikk.butikknavn}</td>
                                                      <td>{hb.count}</td>
                                                  </tr>
                                                 )}
                        </tbody>
                    </table>
                </div>
            </Container>
        </div>
    );
}
