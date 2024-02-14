import React from 'react';
import { Container } from './bootstrap/Container';
import { StyledLinkLeft } from './bootstrap/StyledLinkLeft';
import { StyledLinkRight } from './bootstrap/StyledLinkRight';

function Favoritter() {
    return (
        <div>
            <nav className="flex items-center justify-between flex-wrap bg-slate-100 p-6">
                <StyledLinkLeft to="/">Opp til matregnskap</StyledLinkLeft>
                <h1 className="text-3xl font-bold">Favoritter</h1>
                <div>&nbsp;</div>
            </nav>
            <Container>
                <StyledLinkRight className="flex justify-end mb-1" to="/favoritter/leggtil">Legg til favoritt</StyledLinkRight>
                <StyledLinkRight className="flex justify-end mb-1" to="/favoritter/slett">Slett favoritt</StyledLinkRight>
                <StyledLinkRight className="flex justify-end mb-1" to="/favoritter/sorter">Endre rekkefølge på favoritter</StyledLinkRight>
            </Container>
        </div>
    );
}

export default Favoritter;
