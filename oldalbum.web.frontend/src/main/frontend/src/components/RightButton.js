import React from 'react';
import { connect, useDispatch } from 'react-redux';
import { MOVE_ALBUMENTRY_RIGHT_REQUEST } from '../reduxactions';
import ChevronRight from './bootstrap/ChevronRight';

function RightButton(props) {
    const { item } = props;
    const {
        canModifyAlbum,
        albumchildcount,
    } = props;
    const dispatch = useDispatch();

    // Button doesn't show up if: 1. edit not allowed, 2: this is the last entry in the album
    if (!canModifyAlbum || item.sort >= albumchildcount) {
        return null;
    }

    return(<button
               className={props.className}
               type="button"
               onClick={() => dispatch(MOVE_ALBUMENTRY_RIGHT_REQUEST(item))}>
               <ChevronRight/></button>);
}

function mapStateToProps(state, ownProps) {
    const canModifyAlbum = state.canModifyAlbum;
    const { item } = ownProps;
    const albumentries = state.albumentries || {};
    const parentItem = albumentries[item.parent] || {};
    const albumchildcount = parentItem.childcount || 0;
    return {
        canModifyAlbum,
        albumchildcount,
    };
}

export default connect(mapStateToProps)(RightButton);
