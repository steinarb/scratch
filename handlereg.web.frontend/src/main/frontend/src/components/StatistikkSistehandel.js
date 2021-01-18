import React, { Component } from 'react';
import { connect } from 'react-redux';
import moment from 'moment';
import { Link } from 'react-router-dom';
import { Header } from './bootstrap/Header';
import { Container } from './bootstrap/Container';
import { StyledLinkLeft } from './bootstrap/StyledLinkLeft';
import { StyledLinkRight } from './bootstrap/StyledLinkRight';

function StatistikkSistehandel(props) {
    const { sistehandel } = props;

    return (
        <div>
            <StyledLinkLeft to="/handlereg/statistikk">Tilbake</StyledLinkLeft>
            <Header>
                <h1>Siste handel gjort i butikk</h1>
            </Header>
            <Container>
                <div className="table-responsive table-sm table-striped">
                    <table className="table">
                        <thead>
                            <tr>
                                <td>Butikk</td>
                                <td>Sist handlet i</td>
                            </tr>
                        </thead>
                        <tbody>
                            {sistehandel.map((sh) =>
                                             <tr key={'butikk' + sh.butikk.storeId}>
                                                 <td>{sh.butikk.butikknavn}</td>
                                                 <td>{moment(sh.date).format("YYYY-MM-DD")}</td>
                                             </tr>
                                            )}
                        </tbody>
                    </table>
                </div>
            </Container>
        </div>
    );
}

const mapStateToProps = state => {
    const sistehandel = state.sistehandel;
    return {
        sistehandel,
    };
};

StatistikkSistehandel = connect(mapStateToProps)(StatistikkSistehandel);
export default StatistikkSistehandel;
