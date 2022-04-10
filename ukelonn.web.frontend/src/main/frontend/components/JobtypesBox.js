import React from 'react';

function JobtypesBox(props) {
    const {id, className, value, jobtypes, onJobtypeFieldChange } = props;
    return (
        <select multiselect="true" size="10" id={id} className={className} onChange={e => onJobtypeFieldChange(e.target.value)} value={value}>
          {jobtypes.map((val) => <option key={val.id} value={val.id}>{val.transactionTypeName}</option>)}
        </select>
    );
}

export default JobtypesBox;
