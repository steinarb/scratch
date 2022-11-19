import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { MOVE_ALBUMENTRY_RIGHT_REQUEST } from '../reduxactions';
import ChevronRight from './bootstrap/ChevronRight';

export default function RightButton(props) {
    const { item } = props;
    const showEditControls = useSelector(state => state.showEditControls);
    const albumchildcount = useSelector(state => (state.albumentries[item.parent] || {}).childcount || 0);
    const dispatch = useDispatch();

    // Button doesn't show up if: 1. edit not allowed, 2: this is the last entry in the album
    if (!showEditControls || item.sort >= albumchildcount) {
        return null;
    }

    return(<button
               className={props.className}
               type="button"
               onClick={() => dispatch(MOVE_ALBUMENTRY_RIGHT_REQUEST(item))}>
               <ChevronRight/></button>);
}
