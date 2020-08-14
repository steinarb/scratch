import React from 'react';
import { connect } from 'react-redux';
import { push } from 'connected-react-router';
import { stringify } from 'qs';
import { MOVE_ALBUMENTRY_DOWN } from '../reduxactions';
import { webcontext } from '../constants';

function DownButton(props) {
    const { loginresult, albumchildcount, item, onMoveDown } = props;
    // Button doesn't show up if: 1. edit not allowed, 2: this is the last entry in the album
    if (!loginresult.canModifyAlbum || item.sort >= albumchildcount) {
        return null;
    }

    return(<button className="btn btn-default" type="button" onClick={() => onMoveDown(item)}>Move down</button>);
}

function mapStateToProps(state, ownProps) {
    const login = state.login || {};
    const loginresult = login.loginresult || { success: false };
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
        onMoveDown: (item) => { dispatch(MOVE_ALBUMENTRY_DOWN(item)); },
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(DownButton);
