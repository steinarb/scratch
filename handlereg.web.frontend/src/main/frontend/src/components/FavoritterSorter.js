import React from 'react';
import {
    useGetOversiktQuery,
    useGetFavoritterQuery,
    usePostFavorittByttMutation,
} from '../api';
import { Container } from './bootstrap/Container';
import { StyledLinkLeft } from './bootstrap/StyledLinkLeft';
import ChevronTop from './bootstrap/ChevronTop';
import ChevronBottom from './bootstrap/ChevronBottom';

export default function FavoritterSorter() {
    const { data: oversikt = {}, isSuccess: oversiktIsSuccess } = useGetOversiktQuery();
    const { brukernavn } = oversikt;
    const { data: favoritter = [] } = useGetFavoritterQuery(brukernavn, { skip: !oversiktIsSuccess });
    const [ postFavorittBytt ] = usePostFavorittByttMutation();

    const onFavorittBytt = async (forste, andre) => {
        await postFavorittBytt({ forste, andre });
    }

    return (
        <div>
            <nav className="flex items-center justify-between flex-wrap bg-slate-100 p-6">
                <StyledLinkLeft to="/favoritter">Tilbake</StyledLinkLeft>
                <h1 className="sm:text-1xl md:text-3xl font-bold">Sorter favoritter</h1>
                <div>&nbsp;</div>
            </nav>
            <Container>
                {
                    favoritter.map((f, indeks, array) => {
                        const forrige = array[indeks-1];
                        const neste = array[indeks+1];
                        return (
                            <div className="flex flex-row" key={'favoritt_' + f.favouriteid.toString()}>
                                <button className="mb-1 ms-2 me-1 ps-3 pe-3 pb-2 text-center block border border-blue-500 rounded bg-blue-500 hover:bg-blue-700 text-white" disabled={!forrige} onClick={() => onFavorittBytt(f, forrige)}>
                                    <ChevronTop/>
                                </button>
                                <div className="w-full mb-1 ms-1 me-1 ps-2 pe-2 pb-2 text-center block border border-blue-500 rounded bg-blue-500 hover:bg-blue-700 text-white">{f.store.butikknavn}</div>
                                <button className="mb-1 ms-1 me-2 ps-3 pe-3 pb-2 text-center block border border-blue-500 rounded bg-blue-500 hover:bg-blue-700 text-white" disabled={!neste} onClick={() => onFavorittBytt(f, neste)}>
                                    <ChevronBottom/>
                                </button>
                            </div>);
                    })
                }
            </Container>
        </div>
    );
}
