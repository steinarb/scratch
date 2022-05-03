import React from 'react';
import Container from './bootstrap/Container';
import ChevronLeft from './bootstrap/ChevronLeft';
import StyledLinkRight from './bootstrap/StyledLinkRight';

function Home() {
    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <a className="btn btn-primary left-align-cell" href="/authservice/">
                    <ChevronLeft/>&nbsp;Up to authservice top
                </a>
                <h1>User administration</h1>
                <div className="col-sm-2"></div>
            </nav>
            <Container>
                <StyledLinkRight to="/authservice/useradmin/users">Adminstrate users</StyledLinkRight>
                <StyledLinkRight to="/authservice/useradmin/roles">Administrate roles</StyledLinkRight>
                <StyledLinkRight to="/authservice/useradmin/permissions">Administrate permissions</StyledLinkRight>
            </Container>
        </div>
    );
}

export default Home;
