import React from 'react';
import { Container } from './bootstrap/Container';
import { StyledLinkLeft } from './bootstrap/StyledLinkLeft';
import { StyledLinkRight } from './bootstrap/StyledLinkRight';

function Statistikk() {
    return (
        <div>
            <nav className="flex items-center justify-between flex-wrap bg-slate-100 p-6">
                <StyledLinkLeft to="/">Opp til matregnskap</StyledLinkLeft>
                <h1 className="text-3xl font-bold">Statistikk</h1>
                <div>&nbsp;</div>
            </nav>
            <Container>
                <StyledLinkRight className="flex justify-end mb-1" to="/statistikk/sumbutikk">Totalsum pr. butikk</StyledLinkRight>
                <StyledLinkRight className="flex justify-end mb-1" to="/statistikk/handlingerbutikk">Antall handlinger i butikk</StyledLinkRight>
                <StyledLinkRight className="flex justify-end mb-1" to="/statistikk/sistehandel">Siste handel i butikk</StyledLinkRight>
                <StyledLinkRight className="flex justify-end mb-1" to="/statistikk/sumyear">Total handlesum fordelt på år</StyledLinkRight>
                <StyledLinkRight className="flex justify-end" to="/statistikk/sumyearmonth">Total handlesum fordelt på år og måned</StyledLinkRight>
            </Container>
        </div>
    );
}

export default Statistikk;
