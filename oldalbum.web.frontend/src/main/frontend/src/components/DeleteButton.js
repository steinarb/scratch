import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { push } from 'redux-first-history';
import { DELETE_ALBUMENTRY_REQUEST } from '../reduxactions';

export default function DeleteButton(props) {
    const { item } = props;
    const text = useSelector(state => state.displayTexts);
    const showEditControls = useSelector(state => state.showEditControls);
    const parent = useSelector(state => state.albumentries[item.parent]);
    const childentries = useSelector(state => state.childentries[item.id]);
    const parentpath = (parent || {}).path || '';
    const children = childentries || [];
    const dispatch = useDispatch();
    const onDelete = (item, parentpath) => {
        dispatch(DELETE_ALBUMENTRY_REQUEST(item));
        dispatch(push(parentpath));
    };

    // Button doesn't show up if: 1. edit not allowed, 2: this is the root album, 3: this is an album with content
    if (!showEditControls || !item.parent || children.length) {
        return null;
    }

    return(<button
               className={(props.className || '') + ' btn btn-light'}
               type="button"
               onClick={() => onDelete(item, parentpath)}>
               {text.delete}</button>);
}
