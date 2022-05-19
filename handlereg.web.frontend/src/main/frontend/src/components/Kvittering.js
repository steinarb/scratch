import React from 'react';
import { useSelector } from 'react-redux';

export default function Kvittering() {
    const viskvittering = useSelector(state => state.viskvittering);
    const oversikt = useSelector(state => state.oversikt);
    const butikk = useSelector(state => state.butikker.find(b => b.storeId === oversikt.lastTransactionStore));
    if (!viskvittering) {
        return null;
    }

    return (
        <div className='alert alert-warning' role='alert'>
            Handlebeløp {oversikt.lastTransactionAmount} brukt på {butikk.butikknavn} registrert!<br/>
            Totalt handlebeløp denne måneden {oversikt.sumThisMonth}, mot {oversikt.sumPreviousMonth} for hele forrige måned
        </div>
    );
}
