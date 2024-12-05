import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import {
    useGetOversiktQuery,
    useGetHandlingerQuery,
    useGetButikkerQuery,
    usePostNyhandlingMutation,
} from '../api';
import {
    BELOP_ENDRE,
    HOME_BUTIKKNAVN_ENDRE,
    DATO_ENDRE,
} from '../actiontypes';
import { Container } from './bootstrap/Container';
import { StyledLinkRight } from './bootstrap/StyledLinkRight';
import Kvittering from './Kvittering';


export default function Home() {
    const { data: oversikt = {}, isSuccess: oversiktIsSuccess } = useGetOversiktQuery();
    const { data: handlinger = [] } = useGetHandlingerQuery(oversikt.accountid, { skip: !oversiktIsSuccess });
    const username = oversikt.brukernavn;
    const { data: butikker = [] } = useGetButikkerQuery();
    const [ postNyhandling ] = usePostNyhandlingMutation();
    const storeId = useSelector(state => state.storeId);
    const butikknavn = useSelector(state => state.butikknavn);
    const handletidspunkt = useSelector(state => state.handletidspunkt);
    const handledato = handletidspunkt.split('T')[0];
    const belop = useSelector(state => state.belop).toString();
    const dispatch = useDispatch();

    const onRegistrerHandlingClicked = async () => {
        await postNyhandling({ storeId, belop, handletidspunkt, username })
    }

    return (
        <div>
            <nav className="flex items-center justify-between flex-wrap bg-slate-100 p-6">
                <a className="text-center block border border-blue-500 rounded py-2 bg-blue-500 hover:bg-blue-700 text-white" href="../.."><span title="chevron left" aria-hidden="true"></span>&nbsp;Gå hjem!</a>
                <h1 className="text-3xl font-bold">Matregnskap</h1>
                <StyledLinkRight to="/hurtigregistrering">Hurtig</StyledLinkRight>
            </nav>
            <Container>
                <p>Hei {oversikt.fornavn}!</p>
                <p>Dine 5 siste innkjøp, er:</p>
                <div>
                    <table className="table-auto border border-slate-400 w-full">
                        <thead className="bg-slate-50">
                            <tr className="py-4">
                                <th className="border border-slate-300">Dato</th>
                                <th className="border border-slate-300">Beløp</th>
                                <th className="border border-slate-300">Butikk</th>
                            </tr>
                        </thead>
                        <tbody>
                            {handlinger.map((handling) =>
                                            <tr key={handling.transactionId}>
                                                <td className="border border-slate-300">{new Date(handling.handletidspunkt).toISOString().split('T')[0]}</td>
                                                <td className="border border-slate-300">{handling.belop}</td>
                                                <td className="border border-slate-300">{handling.butikk}</td>
                                            </tr>
                                           )}
                        </tbody>
                    </table>
                </div>
                <form className="w-full max-w-lg mt-4 grid grid-flow-row auto-rows-max" onSubmit={ e => { e.preventDefault(); }}>
                    <div className="columns-2 mb-2">
                        <label className="w-full ms-5 block uppercase text-gray-700 font-bold" htmlFor="amount">Nytt beløp</label>
                        <input className="appearance-none w-full bg-gray-200 text-gray-700 border border-red-500 rounded py-3 px-4 focus:outline-none focus:bg-white" id="amount" type="number" pattern="\d+" value={belop} onChange={e => dispatch(BELOP_ENDRE(e.target.value))} />
                    </div>
                    <div className="columns-2 mb-2">
                        <label className="w-full ms-5 block uppercase tracking-wide text-gray-700 font-bold" htmlFor="jobtype">Velg butikk</label>
                        <div className="inline-block relative w-full">
                            <input
                                className="block appearance-none w-full bg-white border border-gray-400 hover:border-gray-500 px-4 py-2 pr-8 rounded shadow leading-tight focus:outline-none focus:shadow-outline"
                                list="butikker"
                                id="valgt-butikk"
                                name="valgt-butikk"
                                value={butikknavn}
                                onChange={e => dispatch(HOME_BUTIKKNAVN_ENDRE({ navn: e.target.value, butikker }))}/>
                            <datalist id="butikker">
                                <option key="-1" value="" />
                                {butikker.map(butikk => <option key={butikk.storeId} value={butikk.butikknavn}/>)}
                            </datalist>
                        </div>
                    </div>
                    <div className="columns-2 mb-2">
                        <label className="w-full ms-5 block uppercase tracking-wide text-gray-700 font-bold" htmlFor="date">Dato</label>
                        <div>
                            <input
                                className="appearance-none block w-full bg-gray-200 text-gray-700 border border-red-500 rounded py-3 px-4 mb-3 leading-tight focus:outline-none focus:bg-white"
                                id="date"
                                type="date"
                                value={handledato}
                                onChange={e => dispatch(DATO_ENDRE(e.target.value))}
                            />
                        </div>
                    </div>
                    <div className="columns-2 mb-2">
                        <div className="w-full">&nbsp;</div>
                        <button className="w-full bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded" disabled={belop <= 0} onClick={onRegistrerHandlingClicked}>Registrer handling</button>
                    </div>
                </form>
                <Kvittering/>
            </Container>
            <Container>
                <StyledLinkRight className="flex justify-end mb-1" to="/statistikk">Statistikk</StyledLinkRight>
                <StyledLinkRight className="flex justify-end mb-1" to="/nybutikk">Ny butikk</StyledLinkRight>
                <StyledLinkRight className="flex justify-end mb-1" to="/endrebutikk">Endre butikk</StyledLinkRight>
                <StyledLinkRight className="flex justify-end" to="/favoritter">Favoritter</StyledLinkRight>
            </Container>
        </div>
    );
}
