import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import {
    useGetButikkerQuery,
    usePostEndrebutikkMutation,
} from '../api';
import { Container } from './bootstrap/Container';
import { StyledLinkLeft } from './bootstrap/StyledLinkLeft';
import {
    VELG_BUTIKK,
    BUTIKKNAVN_ENDRE,
} from '../actiontypes';


const uvalgtButikk = { storeId: -1, butikknavn: '', gruppe: 2 };

export default function EndreButikk() {
    const valgtButikk = useSelector(state => state.valgtButikk);
    const butikk = useSelector(state => state.butikk);
    const butikknavn = useSelector(state => state.butikknavn);
    const { data: butikker = [] } = useGetButikkerQuery();
    const [ postEndrebutikk ] = usePostEndrebutikkMutation();
    const dispatch = useDispatch();

    const onLagreEndretButikkClicked = async () => {
        await postEndrebutikk({ ...butikk, butikknavn });
    }

    return (
        <div>
            <nav className="flex items-center justify-between flex-wrap bg-slate-100 p-6">
                <StyledLinkLeft to="/">Opp til matregnskap</StyledLinkLeft>
                <h1 className="sm:text-2xl md:text-3xl font-bold">Endre butikk</h1>
                <div>&nbsp;</div>
            </nav>
            <Container>
                <form className="w-full max-w-lg mt-4 grid grid-flow-row auto-rows-max" onSubmit={ e => { e.preventDefault(); }}>
                    <select className="block appearance-none w-full bg-white border border-gray-400 hover:border-gray-500 px-4 py-2 pr-8 rounded shadow leading-tight focus:outline-none focus:shadow-outline" size="10" value={valgtButikk} onChange={e => dispatch(VELG_BUTIKK(butikker.find(b => b.storeId.toString()===e.target.value) || uvalgtButikk))}>
                        { butikker.map((b, indeks) => <option key={'butikk_' + b.storeId.toString()} value={b.storeId.toString()}>{b.butikknavn}</option>) }
                    </select>
                    <div className="columns-2 mb-2">
                        <label className="w-full ms-5 block uppercase text-gray-700 font-bold" htmlFor="amount">Butikknavn</label>
                        <input className="appearance-none w-full bg-gray-200 text-gray-700 border border-red-500 rounded py-3 px-4 focus:outline-none focus:bg-white" id="amount" type="text" value={butikknavn} onChange={e => dispatch(BUTIKKNAVN_ENDRE(e.target.value))} />
                    </div>
                    <div className="columns-2 mb-2">
                        <div className="w-full">&nbsp;</div>
                        <button className="w-full bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded" onClick={onLagreEndretButikkClicked}>Lagre endret butikk</button>
                    </div>
                </form>
            </Container>
        </div>
    );
}
