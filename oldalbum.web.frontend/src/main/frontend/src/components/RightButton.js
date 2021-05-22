import React from 'react';
import { connect } from 'react-redux';
import { MOVE_ALBUMENTRY_RIGHT } from '../reduxactions';
import ChevronRight from './bootstrap/ChevronRight';

function RightButton(props) {
    const { loginresult, albumchildcount, item, onMoveRight } = props;
    // Button doesn't show up if: 1. edit not allowed, 2: this is the last entry in the album
    if (!loginresult.canModifyAlbum || item.sort >= albumchildcount) {
        return null;
    }

    return(<button className={props.className} type="button" onClick={() => onMoveRight(item)}><ChevronRight/></button>);
}

function mapStateToProps(state, ownProps) {
    const loginresult = state.loginresult;
    const { item } = ownProps;
    const albumentries = state.albumentries || {};
    const parentItem = albumentries[item.parent] || {};
    const albumchildcount = parentItem.childcount || 0;
    return {
        loginresult,
        albumchildcount,
    };
}

function mapDispatchToProps(dispatch) {
    return {
        onMoveRight: (item) => { dispatch(MOVE_ALBUMENTRY_RIGHT(item)); },
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(RightButton);
