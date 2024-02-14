import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { Container } from './bootstrap/Container';
import { StyledLinkLeft } from './bootstrap/StyledLinkLeft';
import {
    VELG_FAVORITTBUTIKK,
    LEGG_TIL_FAVORITT,
} from '../actiontypes';

export default function FavoritterLeggTil() {
    const butikker = useSelector(state => state.butikker);
    const favoritter = useSelector(state => state.favoritter);
    const favorittbutikk = useSelector(state => state.favorittbutikk);
    const ledigeButikker = butikker.filter(butikk => !favoritter.find(fav => fav.store.storeId === butikk.storeId));
    const ingenButikkValgt = favorittbutikk === -1;
    const dispatch = useDispatch();

    return (
        <div>
            <nav className="flex items-center justify-between flex-wrap bg-slate-100 p-6">
                <StyledLinkLeft to="/favoritter">Tilbake</StyledLinkLeft>
                <h1 className="sm:text-1xl md:text-3xl font-bold">Legg til favoritt-butikk</h1>
                <div>&nbsp;</div>
            </nav>
            <Container>
                <div>
                    { favoritter.map(f => <div className="flex mb-1 ms-2 me-2 ps-4 text-center block border border-blue-500 rounded py-2 bg-blue-500 hover:bg-blue-700 text-white" key={'favoritt_' + f.favouriteid}>{f.store.butikknavn}</div>) }
                </div>
                <form onSubmit={ e => { e.preventDefault(); }}>
                    <select className="block appearance-none bg-white border border-gray-400 hover:border-gray-500 ms-2 px-4 py-2 pe-8 rounded shadow leading-tight focus:outline-none focus:shadow-outline" value={favorittbutikk} onChange={e => dispatch(VELG_FAVORITTBUTIKK(parseInt(e.target.value)))}>
                        <option key="butikk_-1" value="-1" />
                        { ledigeButikker.map(b => <option key={'butikk_' + b.storeId.toString()} value={b.storeId}>{b.butikknavn}</option>) }
                    </select>
                    <div>
                        <button className="ms-1 mt-2 ps-4 pe-4 text-center block border border-blue-500 rounded py-2 bg-blue-500 hover:bg-blue-700 text-white" disabled={ingenButikkValgt} onClick={() => dispatch(LEGG_TIL_FAVORITT())}>Legg til favoritt</button>
                    </div>
                </form>
            </Container>
        </div>
    );
}
