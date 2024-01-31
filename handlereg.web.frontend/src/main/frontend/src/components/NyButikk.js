import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { Container } from './bootstrap/Container';
import { StyledLinkLeft } from './bootstrap/StyledLinkLeft';
import {
    BUTIKKNAVN_ENDRE,
    NYBUTIKK_REGISTRER,
} from '../actiontypes';

export default function NyButikk() {
    const butikknavn = useSelector(state => state.butikknavn);
    const dispatch = useDispatch();

    return (
        <div>
            <nav className="flex items-center justify-between flex-wrap bg-slate-100 p-6">
                <StyledLinkLeft to="/handlereg">Opp til matregnskap</StyledLinkLeft>
                <h1 className="text-3xl font-bold">Ny butikk</h1>
                <div>&nbsp;</div>
            </nav>
            <Container>
                <form className="w-full max-w-lg mt-4 grid grid-flow-row auto-rows-max" onSubmit={ e => { e.preventDefault(); }}>
                    <div className="columns-2 mb-2">
                        <label className="w-full ms-5 block uppercase text-gray-700 font-bold" htmlFor="amount">Ny butikk</label>
                        <input className="appearance-none w-full bg-gray-200 text-gray-700 border border-red-500 rounded py-3 px-4 focus:outline-none focus:bg-white" id="amount" type="text" value={butikknavn} onChange={e => dispatch(BUTIKKNAVN_ENDRE(e.target.value))} />
                    </div>
                    <div className="columns-2 mb-2">
                        <div className="w-full">&nbsp;</div>
                        <button className="w-full bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded" onClick={() => dispatch(NYBUTIKK_REGISTRER(butikknavn))}>Legg til butikk</button>
                    </div>
                </form>
            </Container>
        </div>
    );
}
