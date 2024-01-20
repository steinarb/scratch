import React from 'react';
import { useSelector } from 'react-redux';
import { Container } from './bootstrap/Container';
import { StyledLinkLeft } from './bootstrap/StyledLinkLeft';

export default function StatistikkSumbutikk() {
    const sumbutikk = useSelector(state => state.sumbutikk);

    return (
        <div>
            <nav>
                <StyledLinkLeft to="/handlereg/statistikk">Tilbake</StyledLinkLeft>
                <h1>Total handlesum fordelt på butikk</h1>
                <div></div>
            </nav>
            <Container>
                <div>
                    <table>
                        <thead>
                            <tr>
                                <td>Butikk</td>
                                <td>Total handlesum</td>
                            </tr>
                        </thead>
                        <tbody>
                            {sumbutikk.map((sb) =>
                                           <tr key={'butikk' + sb.butikk.storeId}>
                                               <td>{sb.butikk.butikknavn}</td>
                                               <td>{sb.sum}</td>
                                           </tr>
                                          )}
                        </tbody>
                    </table>
                </div>
            </Container>
        </div>
    );
}
