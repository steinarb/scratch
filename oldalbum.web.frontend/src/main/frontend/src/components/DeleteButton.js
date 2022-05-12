import React from 'react';
import { connect, useDispatch } from 'react-redux';
import { push } from 'connected-react-router';
import { DELETE_ITEM_REQUEST } from '../reduxactions';

function DeleteButton(props) {
    const { item } = props;
    const {
        canModifyAlbum,
        parentpath,
        children,
    } = props;
    const dispatch = useDispatch();
    const onDelete = (item, parentpath) => {
        dispatch(DELETE_ITEM_REQUEST(item));
        dispatch(push(parentpath));
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

function mapStateToProps(state, ownProps) {
    const canModifyAlbum = state.canModifyAlbum;
    const { item } = ownProps;
    const albumentries = state.albumentries || {};
    const parentItem = albumentries[item.parent] || {};
    const parentpath = parentItem.path || '';
    const childentries = state.childentries || {};
    const children = childentries[item.id] || [];
    return {
        canModifyAlbum,
        parentpath,
        children,
    };
}

export default connect(mapStateToProps)(DeleteButton);
