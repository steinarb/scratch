import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import {
    SET_ALBUM_SHOW_YEARS,
    SET_ALBUM_HIDE_YEARS,
} from '../reduxactions';

export default function AlbumHideShowYearsButton(props) {
    const { album } = props;
    const { id } = album;
    const text = useSelector(state => state.displayTexts);
    const albumGroupByYear = useSelector(state => !!state.albumGroupByYear[id]);
    const ariaControls = useSelector(state => id in state.childentriesByYear ? Object.keys(state.childentriesByYear[id]).map(k => 'collapse' + k.toString()).join(' ') : []);
    const expanded = useSelector(state => id in state.albumShowYears ? state.albumShowYears[id] : true);
    const dispatch = useDispatch();
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
            onClick={() => dispatch(expanded ? SET_ALBUM_HIDE_YEARS(id) : SET_ALBUM_SHOW_YEARS(id))}
        >
            {labelText}
        </button>
    );
}
