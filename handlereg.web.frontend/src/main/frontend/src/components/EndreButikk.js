import React from 'react';
import { connect, useDispatch } from 'react-redux';
import { Container } from './bootstrap/Container';
import { StyledLinkLeft } from './bootstrap/StyledLinkLeft';
import {
    BUTIKKNAVN_ENDRE,
    VELG_BUTIKK,
    BUTIKK_LAGRE,
} from '../actiontypes';

function EndreButikk(props) {
    const { valgtButikk, butikk, butikker } = props;
    const dispatch = useDispatch();
    const { butikknavn } = butikk;
    const butikkerUnntattUndefined = butikker.filter(b => b.storeId > -1);

    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <StyledLinkLeft to="/handlereg">Opp til matregnskap</StyledLinkLeft>
                <h1>Endre butikk</h1>
                <div className="col-sm-2"></div>
            </nav>
            <Container>
                <form onSubmit={ e => { e.preventDefault(); }}>
                    <select size="10" value={valgtButikk} onChange={e => dispatch(VELG_BUTIKK({ indeks: e.target.value, butikker: butikkerUnntattUndefined}))}>
                        { butikkerUnntattUndefined.map((b, indeks) => <option key={'butikk_' + b.storeId.toString()} value={indeks}>{b.butikknavn}</option>) }
                    </select>
                    <div className="form-group row">
                        <label htmlFor="amount" className="col-form-label col-5">Ny butikk</label>
                        <div className="col-7">
                            <input id="amount" className="form-control" type="text" value={butikknavn} onChange={e => dispatch(BUTIKKNAVN_ENDRE(e.target.value))} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <div className="col-5"/>
                        <div className="col-7">
                            <button className="btn btn-primary" onClick={() => dispatch(BUTIKK_LAGRE(butikk))}>Lagre endret butikk</button>
                        </div>
                    </div>
                </form>
            </Container>
        </div>
    );
}

const mapStateToProps = state => {
    const { valgtButikk, butikk, butikker } = state;
    return {
        valgtButikk,
        butikk,
        butikker,
    };
};

export default connect(mapStateToProps)(EndreButikk);
