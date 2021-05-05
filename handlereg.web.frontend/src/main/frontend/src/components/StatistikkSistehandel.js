import React from 'react';
import { connect } from 'react-redux';
import moment from 'moment';
import { Container } from './bootstrap/Container';
import { StyledLinkLeft } from './bootstrap/StyledLinkLeft';

function StatistikkSistehandel(props) {
    const { sistehandel } = props;

    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <StyledLinkLeft to="/handlereg/statistikk">Tilbake</StyledLinkLeft>
                <h1>Siste handel gjort i butikk</h1>
                <div className="col-sm-2"></div>
            </nav>
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

export default connect(mapStateToProps)(StatistikkSistehandel);
