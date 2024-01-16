import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { MOVE_ALBUMENTRY_UP_REQUEST } from '../reduxactions';

export default function UpButton(props) {
    const { item } = props;
    const showEditControls = useSelector(state => state.showEditControls);
    const dispatch = useDispatch();
    // Button doesn't show up if: 1. edit not allowed, 2: this is the first entry in the album
    if (!showEditControls || item.sort < 2) {
        return null;
    }

    return(
        <div
            className={props.className}
            onClick={() => dispatch(MOVE_ALBUMENTRY_UP_REQUEST(item))}>
            <span className="oi oi-chevron-top" title="chevron top" aria-hidden="true"></span>
        </div>
    );
}
