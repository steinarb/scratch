import React from 'react';
import { useSelector } from 'react-redux';
import { Container } from './bootstrap/Container';
import { StyledLinkLeft } from './bootstrap/StyledLinkLeft';

export default function StatistikkHandlingerbutikk() {
    const handlingerbutikk = useSelector(state => state.handlingerbutikk);

    return (
        <div>
            <nav className="flex items-center justify-between flex-wrap bg-slate-100 p-6">
                <StyledLinkLeft to="/handlereg/statistikk">Tilbake</StyledLinkLeft>
                <h1 className="sm:text-1xl md:text-3xl font-bold">Antall handlinger gjort i butikk</h1>
                <div>&nbsp;</div>
            </nav>
            <Container>
                <div>
                    <table className="table-auto border border-slate-400 w-full">
                        <thead className="bg-slate-50">
                            <tr className="py-4">
                                <td className="border border-slate-300">Butikk</td>
                                <td className="border border-slate-300">Antall handlinger</td>
                            </tr>
                        </thead>
                        <tbody>
                            {handlingerbutikk.map((hb) =>
                                                  <tr key={'butikk' + hb.butikk.storeId}>
                                                      <td className="border border-slate-300">{hb.butikk.butikknavn}</td>
                                                      <td className="border border-slate-300">{hb.count}</td>
                                                  </tr>
                                                 )}
                        </tbody>
                    </table>
                </div>
            </Container>
        </div>
    );
}
