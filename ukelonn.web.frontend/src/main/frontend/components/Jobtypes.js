import React from 'react';

function Jobtypes(props) {
    const {id, className, value, jobtypes, onJobtypeFieldChange } = props;
    return (
        <select id={id} className={className} onChange={(event) => onJobtypeFieldChange(event.target.value, jobtypes)} value={value}>
            <option key="-1" value="-1" />
            {jobtypes.map((val) => <option key={val.id} value={val.id}>{val.transactionTypeName}</option>)}
        </select>
    );
}

export default Jobtypes;
