import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { MOVE_ALBUMENTRY_LEFT_REQUEST } from '../reduxactions';
import ChevronLeft from './bootstrap/ChevronLeft';

export default function LeftButton(props) {
    const { item, className='' } = props;
    const showEditControls = useSelector(state => state.showEditControls);
    const dispatch = useDispatch();

    // Button doesn't show up if: 1. edit not allowed, 2: this is the first entry in the album
    if (!showEditControls || item.sort < 2) {
        return null;
    }

    return(
        <div className={className + ' d-none d-md-flex btn-group-vertical'}>
            <button type="button" onClick={() => dispatch(MOVE_ALBUMENTRY_LEFT_REQUEST(item))}>
                <ChevronLeft/>
            </button>
        </div>
    );
}
