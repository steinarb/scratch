import React from 'react';

function Amount(props) {
    const {id, className, transactionAmount, onAmountFieldChange } = props;
    return (
        <input id={id} className={className} type="text" value={transactionAmount} onChange={(event) => onAmountFieldChange(event.target.value)} />
    );
}

export default Amount;
