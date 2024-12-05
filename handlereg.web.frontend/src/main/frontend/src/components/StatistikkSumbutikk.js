import React from 'react';
import { useGetSumButikkQuery } from '../api';
import { Container } from './bootstrap/Container';
import { StyledLinkLeft } from './bootstrap/StyledLinkLeft';

export default function StatistikkSumbutikk() {
    const { data: sumbutikk = [] } = useGetSumButikkQuery();

    return (
        <div>
            <nav className="flex items-center justify-between flex-wrap bg-slate-100 p-6">
                <StyledLinkLeft to="/statistikk">Tilbake</StyledLinkLeft>
                <h1 className="sm:text-1xl md:text-3xl font-bold">Total handlesum fordelt på butikk</h1>
                <div>&nbsp;</div>
            </nav>
            <Container>
                <div>
                    <table className="table-auto border border-slate-400 w-full">
                        <thead className="bg-slate-50">
                            <tr className="py-4">
                                <td className="border border-slate-300">Butikk</td>
                                <td className="border border-slate-300">Total handlesum</td>
                            </tr>
                        </thead>
                        <tbody>
                            {sumbutikk.map((sb) =>
                                           <tr key={'butikk' + sb.butikk.storeId}>
                                               <td className="border border-slate-300">{sb.butikk.butikknavn}</td>
                                               <td className="border border-slate-300">{sb.sum}</td>
                                           </tr>
                                          )}
                        </tbody>
                    </table>
                </div>
            </Container>
        </div>
    );
}
