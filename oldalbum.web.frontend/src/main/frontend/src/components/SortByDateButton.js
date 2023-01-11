import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { SORT_ALBUM_ENTRIES_BY_DATE_REQUEST } from '../reduxactions';

export default function SortByDateButton(props) {
    const { item } = props;
    const showEditControls = useSelector(state => state.showEditControls);
    const dispatch = useDispatch();

    // Button doesn't show up if: 1. edit not allowed
    if (!showEditControls) {
        return null;
    }

    return(
        <button
            className={(props.className || '') + ' btn btn-primary'}
            type="button"
            onClick={() => dispatch(SORT_ALBUM_ENTRIES_BY_DATE_REQUEST(item))}>
            Sort by date</button>
    );
}
