import React from 'react';
import { connect } from 'react-redux';
import { Container } from './bootstrap/Container';
import { StyledLinkLeft } from './bootstrap/StyledLinkLeft';
import ChevronTop from './bootstrap/ChevronTop';
import ChevronBottom from './bootstrap/ChevronBottom';
import {
    BYTT_FAVORITTER,
} from '../actiontypes';

function FavoritterSorter(props) {
    const { favoritter, bytt } = props;

    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <StyledLinkLeft to="/handlereg/favoritter">Tilbake</StyledLinkLeft>
                <h1>Sorter favoritter</h1>
                <div className="col-sm-2"></div>
            </nav>
            <Container>
                {
                    favoritter.map((f, indeks, array) => {
                        const forrige = array[indeks-1];
                        const neste = array[indeks+1];
                        return (
                            <div key={'favoritt_' + f.favouriteid.toString()} className="row">
                                <button className="col btn btn-primary m-1" disabled={!forrige} onClick={() => bytt(f, forrige)}>
                                    <ChevronTop/>
                                </button>
                                <div className="col-8 btn btn-primary m-1 left-align-cell">{f.store.butikknavn}</div>
                                <button className="col btn btn-primary m-1" disabled={!neste} onClick={() => bytt(f, neste)}>
                                    <ChevronBottom/>
                                </button>
                            </div>);
                    })
                }
            </Container>
        </div>
    );
}

function mapStateToProps(state) {
    const favoritter = state.favoritter;
    return {
        favoritter,
    };
}

function mapDispatchToProps(dispatch) {
    return {
        bytt: (forste, andre) => dispatch(BYTT_FAVORITTER({ forste, andre }))
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(FavoritterSorter);
