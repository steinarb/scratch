import React from 'react';
import { Link } from 'react-router-dom';
import { Header } from './bootstrap/Header';
import { Container } from './bootstrap/Container';
import { StyledLinkLeft } from './bootstrap/StyledLinkLeft';
import { StyledLinkRight } from './bootstrap/StyledLinkRight';

function Statistikk(props) {
    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <StyledLinkLeft to="/handlereg">Opp til matregnskap</StyledLinkLeft>
                <h1>Statistikk</h1>
                <div className="col-sm-2"></div>
            </nav>
            <Container>
                <StyledLinkRight to="/handlereg/statistikk/sumbutikk">Totalsum pr. butikk</StyledLinkRight>
                <StyledLinkRight to="/handlereg/statistikk/handlingerbutikk">Antall handlinger i butikk</StyledLinkRight>
                <StyledLinkRight to="/handlereg/statistikk/sistehandel">Siste handel i butikk</StyledLinkRight>
                <StyledLinkRight to="/handlereg/statistikk/sumyear">Total handlesum fordelt på år</StyledLinkRight>
                <StyledLinkRight to="/handlereg/statistikk/sumyearmonth">Total handlesum fordelt på år og måned</StyledLinkRight>
            </Container>
        </div>
    );
}

export default Statistikk;
