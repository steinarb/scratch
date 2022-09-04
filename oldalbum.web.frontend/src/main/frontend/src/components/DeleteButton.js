import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { push } from 'redux-first-history';
import { DELETE_ITEM_REQUEST } from '../reduxactions';

export default function DeleteButton(props) {
    const { item } = props;
    const canModifyAlbum = useSelector(state => state.canModifyAlbum);
    const basename = useSelector(state => state.basename);
    const parentpath = useSelector(state => (state.albumentries[item.parent] || {}).path || '');
    const children = useSelector(state => state.childentries[item.id] || []);
    const dispatch = useDispatch();
    const onDelete = (item, parentpath) => {
        dispatch(DELETE_ITEM_REQUEST(item));
        dispatch(push(basename + parentpath));
    };

    // Button doesn't show up if: 1. edit not allowed, 2: this is the root album, 3: this is an album with content
    if (!canModifyAlbum || !item.parent || children.length) {
        return null;
    }

    return(<button
               className={(props.className || '') + ' btn btn-primary'}
               type="button"
               onClick={() => onDelete(item, parentpath)}>
               Delete</button>);
}
