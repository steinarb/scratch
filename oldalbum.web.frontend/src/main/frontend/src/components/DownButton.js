import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { MOVE_ALBUMENTRY_DOWN_REQUEST } from '../reduxactions';

export default function DownButton(props) {
    const { item } = props;
    const canModifyAlbum = useSelector(state => state.canModifyAlbum);
    const albumchildcount = useSelector(state => (state.albumentries[item.parent] || {}).childcount || 0);
    const dispatch = useDispatch();

    // Button doesn't show up if: 1. edit not allowed, 2: this is the last entry in the album
    if (!canModifyAlbum || item.sort >= albumchildcount) {
        return null;
    }

    return(<button
               className={props.className}
               type="button"
               onClick={() => dispatch(MOVE_ALBUMENTRY_DOWN_REQUEST(item))}>
               <span className="oi oi-chevron-bottom" title="chevron top" aria-hidden="true"></span>
           </button>);
}
