import React from 'react';
import { useSelector } from 'react-redux';
import { Container } from './bootstrap/Container';
import { StyledLinkLeft } from './bootstrap/StyledLinkLeft';

export default function StatistikkSumyear() {
    const sumyear = useSelector(state => state.sumyear);

    return (
        <div>
            <nav>
                <StyledLinkLeft to="/handlereg/statistikk">Tilbake</StyledLinkLeft>
                <h1>Handlesum pr år</h1>
                <div></div>
            </nav>
            <Container>
                <div>
                    <table>
                        <thead>
                            <tr>
                                <td>År</td>
                                <td>Handlebeløp</td>
                            </tr>
                        </thead>
                        <tbody>
                            {sumyear.map((sy) =>
                                         <tr key={'year' + sy.year}>
                                             <td>{sy.year}</td>
                                             <td>{sy.sum}</td>
                                         </tr>
                                        )}
                        </tbody>
                    </table>
                </div>
            </Container>
        </div>
    );
}
