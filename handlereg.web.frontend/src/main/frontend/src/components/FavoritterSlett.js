import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { Container } from './bootstrap/Container';
import { StyledLinkLeft } from './bootstrap/StyledLinkLeft';
import {
    SLETT_FAVORITT,
} from '../actiontypes';

export default function FavoritterSlett() {
    const favoritter = useSelector(state => state.favoritter);
    const dispatch = useDispatch();

    return (
        <div>
            <nav className="flex items-center justify-between flex-wrap bg-slate-100 p-6">
                <StyledLinkLeft to="/favoritter">Tilbake</StyledLinkLeft>
                <h1 className="sm:text-1xl md:text-3xl font-bold">Slett butikk(er) fra favoritt-lista</h1>
                <div>&nbsp;</div>
            </nav>
            <Container>
                { favoritter.map(f => <button className="flex w-80 mb-1 ms-2 me-2 ps-4 text-center block border border-blue-500 rounded py-2 bg-blue-500 hover:bg-blue-700 text-white" key={'favoritt_' + f.favouriteid} onClick={() => dispatch(SLETT_FAVORITT(f))}>{f.store.butikknavn}</button>) }
            </Container>
        </div>
    );
}
