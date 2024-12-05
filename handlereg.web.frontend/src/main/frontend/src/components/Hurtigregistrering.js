import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import {
    useGetOversiktQuery,
    useGetHandlingerQuery,
    useGetFavoritterQuery,
    usePostNyhandlingMutation,
} from '../api';
import { Container } from './bootstrap/Container';
import { StyledLinkLeft } from './bootstrap/StyledLinkLeft';
import Kvittering from './Kvittering';
import {
    BELOP_ENDRE,
} from '../actiontypes';

export default function Hurtigregistrering() {
    const { data: oversikt = {}, isSuccess: oversiktIsSuccess } = useGetOversiktQuery();
    const { brukernavn: username } = oversikt;
    const { data: favoritter = [] } = useGetFavoritterQuery(oversikt.brukernavn, { skip: !oversiktIsSuccess });
    const handletidspunkt = useSelector(state => state.handletidspunkt);
    const belop = useSelector(state => state.belop).toString();
    const [ postNyhandling ] = usePostNyhandlingMutation();
    const dispatch = useDispatch();

    const onStoreClicked = async (storeId) => {
        await postNyhandling({ storeId, belop, handletidspunkt, username })
    }

    return (
        <div>
            <nav className="flex items-center justify-between flex-wrap bg-slate-100 p-6">
                <StyledLinkLeft to="/">Opp til matregnskap</StyledLinkLeft>
                <h1 className="sm:text-1xl md:text-3xl font-bold">Hurtigregistrering</h1>
                <div>&nbsp;</div>
            </nav>
            <Container>
                <form className="w-full max-w-lg mt-4 grid grid-flow-row auto-rows-max" onSubmit={ e => { e.preventDefault(); }}>
                    <div className="columns-2 mb-2">
                        <label className="w-full ms-5 block uppercase text-gray-700 font-bold" htmlFor="amount">Nytt beløp</label>
                        <input className="appearance-none w-full bg-gray-200 text-gray-700 border border-red-500 rounded py-3 px-4 focus:outline-none focus:bg-white" id="amount" type="number" pattern="\d+" value={belop} onChange={e => dispatch(BELOP_ENDRE(e.target.value))} />
                    </div>
                    <Kvittering/>
                    { favoritter.map(f => <button className="flex w-80 mb-1 ms-2 me-2 ps-4 text-center block border border-blue-500 rounded py-2 bg-blue-500 hover:bg-blue-700 text-white" key={'favoritt_' + f.favouriteid.toString()} disabled={belop <= 0} onClick={() => onStoreClicked(f.store.storeId)}>{f.store.butikknavn}</button>) }
                </form>
            </Container>
        </div>
    );
}
