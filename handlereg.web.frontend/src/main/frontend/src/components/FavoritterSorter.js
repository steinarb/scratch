import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { Container } from './bootstrap/Container';
import { StyledLinkLeft } from './bootstrap/StyledLinkLeft';
import ChevronTop from './bootstrap/ChevronTop';
import ChevronBottom from './bootstrap/ChevronBottom';
import {
    BYTT_FAVORITTER,
} from '../actiontypes';

export default function FavoritterSorter() {
    const favoritter = useSelector(state => state.favoritter);
    const dispatch = useDispatch();

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
                                <button className="col btn btn-primary m-1" disabled={!forrige} onClick={() => dispatch(BYTT_FAVORITTER({ forste: f, andre: forrige}))}>
                                    <ChevronTop/>
                                </button>
                                <div className="col-8 btn btn-primary m-1 left-align-cell">{f.store.butikknavn}</div>
                                <button className="col btn btn-primary m-1" disabled={!neste} onClick={() => dispatch(BYTT_FAVORITTER({ forste: f, andre: neste }))}>
                                    <ChevronBottom/>
                                </button>
                            </div>);
                    })
                }
            </Container>
        </div>
    );
}
