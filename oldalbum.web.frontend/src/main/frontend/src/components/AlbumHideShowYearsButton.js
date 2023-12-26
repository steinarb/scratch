import React, { useState } from 'react';
import { useSelector } from 'react-redux';

export default function AlbumHideShowYearsButton(props) {
    const { album } = props;
    const { id } = album;
    const text = useSelector(state => state.displayTexts);
    const albumGroupByYear = useSelector(state => !!state.albumGroupByYear[id]);
    const ariaControls = useSelector(state => id in state.childentriesByYear ? Object.keys(state.childentriesByYear[id]).map(k => 'collapse' + k.toString()).join(' ') : []);
    const [ expanded, setExpanded ] = useState(true);
    const labelText = expanded ? text.hideAllYears : text.showAllYears;

    if (!albumGroupByYear) {
        return null;
    }

    return(
        <button
            className={props.className}
            type="button"
            data-toggle="collapse"
            data-target=".multi-collapse"
            aria-expanded="false"
            aria-controls={ariaControls}
            onClick={() => setExpanded(!expanded)}
        >
            {labelText}
        </button>
    );
}
