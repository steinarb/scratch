import React from 'react';
import { useSelector } from 'react-redux';
import { Container } from './bootstrap/Container';
import { StyledLinkLeft } from './bootstrap/StyledLinkLeft';

export default function StatistikkSumyearmonth() {
    const sumyearmonth = useSelector(state => state.sumyearmonth);

    return (
        <div>
            <nav className="flex items-center justify-between flex-wrap bg-slate-100 p-6">
                <StyledLinkLeft to="/statistikk">Tilbake</StyledLinkLeft>
                <h1 className="sm:text-1xl md:text-3xl font-bold">Handlesum for år og måned</h1>
                <div>&nbsp;</div>
            </nav>
            <Container>
                <div>
                    <table className="table-auto border border-slate-400 w-full">
                        <thead className="bg-slate-50">
                            <tr className="py-4">
                                <td className="border border-slate-300">År</td>
                                <td className="border border-slate-300">Måned</td>
                                <td className="border border-slate-300">Handlebeløp</td>
                            </tr>
                        </thead>
                        <tbody>
                            {sumyearmonth.map((sym) =>
                                              <tr key={'year' + sym.year + sym.month}>
                                                  <td className="border border-slate-300">{sym.year}</td>
                                                  <td className="border border-slate-300">{sym.month}</td>
                                                  <td className="border border-slate-300">{sym.sum}</td>
                                              </tr>
                                             )}
                        </tbody>
                    </table>
                </div>
            </Container>
        </div>
    );
}
