import React from 'react';
import { connect, useDispatch } from 'react-redux';
import { Container } from './bootstrap/Container';
import { StyledLinkLeft } from './bootstrap/StyledLinkLeft';
import {
    BUTIKKNAVN_ENDRE,
    NYBUTIKK_REGISTRER,
} from '../actiontypes';

function NyButikk(props) {
    const { butikknavn } = props;
    const dispatch = useDispatch();

    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <StyledLinkLeft to="/handlereg">Opp til matregnskap</StyledLinkLeft>
                <h1>Ny butikk</h1>
                <div className="col-sm-2"></div>
            </nav>
            <Container>
                <form onSubmit={ e => { e.preventDefault(); }}>
                    <div className="form-group row">
                        <label htmlFor="amount" className="col-form-label col-5">Ny butikk</label>
                        <div className="col-7">
                            <input id="amount" className="form-control" type="text" value={butikknavn} onChange={e => dispatch(BUTIKKNAVN_ENDRE(e.target.value))} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <div className="col-5"/>
                        <div className="col-7">
                            <button className="btn btn-primary" onClick={() => dispatch(NYBUTIKK_REGISTRER(butikknavn))}>Legg til butikk</button>
                        </div>
                    </div>
                </form>
            </Container>
        </div>
    );
}

function mapStateToProps(state) {
    const { butikknavn } = state;
    return {
        butikknavn,
    };
}

export default connect(mapStateToProps)(NyButikk);
