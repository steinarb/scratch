import React from 'react';
import { useSelector } from 'react-redux';
import { useGetOversiktQuery, useGetButikkerQuery } from '../api';

export default function Kvittering() {
    const viskvittering = useSelector(state => state.viskvittering);
    const { data: oversikt = {} } = useGetOversiktQuery();
    const { data: butikker = [] } = useGetButikkerQuery();
    const butikk = butikker.find(b => b.storeId === oversikt.lastTransactionStore) || {};
    if (!viskvittering) {
        return null;
    }

    return (
        <div role='alert' className="bg-yellow-100 border border-red-500 rounded py-5 px-5 my-5 mx-5">
            Handlebeløp {oversikt.lastTransactionAmount} brukt på {butikk.butikknavn} registrert!<br/>
            Totalt handlebeløp denne måneden {oversikt.sumThisMonth}, mot {oversikt.sumPreviousMonth} for hele forrige måned
        </div>
    );
}
