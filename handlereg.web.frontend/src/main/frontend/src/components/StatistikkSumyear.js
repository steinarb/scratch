import React from 'react';
import { connect } from 'react-redux';
import { Container } from './bootstrap/Container';
import { StyledLinkLeft } from './bootstrap/StyledLinkLeft';

function StatistikkSumyear(props) {
    const { sumyear } = props;

    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <StyledLinkLeft to="/handlereg/statistikk">Tilbake</StyledLinkLeft>
                <h1>Handlesum pr år</h1>
                <div className="col-sm-2"></div>
            </nav>
            <Container>
                <div className="table-responsive table-sm table-striped">
                    <table className="table">
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

const mapStateToProps = state => {
    const sumyear = state.sumyear;
    return {
        sumyear,
    };
};

export default connect(mapStateToProps)(StatistikkSumyear);
