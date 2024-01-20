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
            <nav>
                <StyledLinkLeft to="/handlereg/favoritter">Tilbake</StyledLinkLeft>
                <h1>Sorter favoritter</h1>
                <div></div>
            </nav>
            <Container>
                {
                    favoritter.map((f, indeks, array) => {
                        const forrige = array[indeks-1];
                        const neste = array[indeks+1];
                        return (
                            <div key={'favoritt_' + f.favouriteid.toString()}>
                                <button disabled={!forrige} onClick={() => dispatch(BYTT_FAVORITTER({ forste: f, andre: forrige}))}>
                                    <ChevronTop/>
                                </button>
                                <div>{f.store.butikknavn}</div>
                                <button disabled={!neste} onClick={() => dispatch(BYTT_FAVORITTER({ forste: f, andre: neste }))}>
                                    <ChevronBottom/>
                                </button>
                            </div>);
                    })
                }
            </Container>
        </div>
    );
}
