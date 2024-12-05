import React from 'react';
import { useGetSisteHandelQuery } from '../api';
import { Container } from './bootstrap/Container';
import { StyledLinkLeft } from './bootstrap/StyledLinkLeft';

export default function StatistikkSistehandel() {
    const { data: sistehandel = [] } = useGetSisteHandelQuery();

    return (
        <div>
            <nav className="flex items-center justify-between flex-wrap bg-slate-100 p-6">
                <StyledLinkLeft to="/statistikk">Tilbake</StyledLinkLeft>
                <h1 className="sm:text-1xl md:text-3xl font-bold">Siste handel gjort i butikk</h1>
                <div>&nbsp;</div>
            </nav>
            <Container>
                <div>
                    <table className="table-auto border border-slate-400 w-full">
                        <thead className="bg-slate-50">
                            <tr className="py-4">
                                <td className="border border-slate-300">Butikk</td>
                                <td className="border border-slate-300">Sist handlet i</td>
                            </tr>
                        </thead>
                        <tbody>
                            {sistehandel.map((sh) =>
                                             <tr key={'butikk' + sh.butikk.storeId}>
                                                 <td className="border border-slate-300">{sh.butikk.butikknavn}</td>
                                                 <td className="border border-slate-300">{new Date(sh.date).toISOString().split('T')[0]}</td>
                                             </tr>
                                            )}
                        </tbody>
                    </table>
                </div>
            </Container>
        </div>
    );
}
