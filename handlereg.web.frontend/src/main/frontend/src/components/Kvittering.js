import React from 'react';
import { connect } from 'react-redux';

function Kvittering(props) {
    const { viskvittering, oversikt } = props;
    if (!viskvittering) {
        return null;
    }

    return (
        <div className='alert alert-warning' role='alert'>
            Handlebeløp {oversikt.lastTransactionAmount} registrert!<br/>
            Totalt handlebeløp denne måneden {oversikt.sumThisMonth}, mot {oversikt.sumPreviousMonth} for hele forrige måned
        </div>
    );
}

function mapStateToProps(state) {
    const { viskvittering, oversikt } = state;
    return {
        viskvittering,
        oversikt,
    };
}

export default connect(mapStateToProps)(Kvittering);
