import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router-dom';
import { Header } from './bootstrap/Header';
import { Container } from './bootstrap/Container';
import { StyledLinkLeft } from './bootstrap/StyledLinkLeft';
import { StyledLinkRight } from './bootstrap/StyledLinkRight';

function StatistikkSumbutikk(props) {
    const { sumbutikk } = props;

    return (
        <div>
            <StyledLinkLeft to="/handlereg/statistikk">Tilbake</StyledLinkLeft>
            <Header>
                <h1>Total handlesum fordelt på butikk</h1>
            </Header>
            <Container>
                <div className="table-responsive table-sm table-striped">
                    <table className="table">
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

const mapStateToProps = state => {
    const sumbutikk = state.sumbutikk;
    return {
        sumbutikk,
    };
};

StatistikkSumbutikk = connect(mapStateToProps)(StatistikkSumbutikk);
export default StatistikkSumbutikk;
