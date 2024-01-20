import React from 'react';
import { useSelector } from 'react-redux';
import { Container } from './bootstrap/Container';
import { StyledLinkLeft } from './bootstrap/StyledLinkLeft';

export default function StatistikkSumyearmonth() {
    const sumyearmonth = useSelector(state => state.sumyearmonth);

    return (
        <div>
            <nav>
                <StyledLinkLeft to="/handlereg/statistikk">Tilbake</StyledLinkLeft>
                <h1>Handlesum for år og måned</h1>
                <div></div>
            </nav>
            <Container>
                <div>
                    <table>
                        <thead>
                            <tr>
                                <td>År</td>
                                <td>Måned</td>
                                <td>Handlebeløp</td>
                            </tr>
                        </thead>
                        <tbody>
                            {sumyearmonth.map((sym) =>
                                              <tr key={'year' + sym.year}>
                                                  <td>{sym.year}</td>
                                                  <td>{sym.month}</td>
                                                  <td>{sym.sum}</td>
                                              </tr>
                                             )}
                        </tbody>
                    </table>
                </div>
            </Container>
        </div>
    );
}
