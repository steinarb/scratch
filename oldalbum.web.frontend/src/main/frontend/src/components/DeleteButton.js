import React from 'react';
import { connect } from 'react-redux';
import { push } from 'connected-react-router';
import { stringify } from 'qs';
import { DELETE_ITEM } from '../reduxactions';
import { webcontext } from '../constants';

function DeleteButton(props) {
    const { loginresult, parentpath, children, item, onDelete } = props;
    // Button doesn't show up if: 1. edit not allowed, 2: this is the root album, 3: this is an album with content
    if (!loginresult.canModifyAlbum || !item.parent || children.length) {
        return null;
    }

    return(<button className="btn btn-default" type="button" onClick={() => onDelete(item, parentpath)}>Delete</button>);
}

function mapStateToProps(state, ownProps) {
    const login = state.login || {};
    const loginresult = login.loginresult || { success: false };
    const { item } = ownProps;
    const albumentries = state.albumentries || {};
    const parentItem = albumentries[item.parent] || {};
    const parentpath = parentItem.path || '';
    const childentries = state.childentries || {};
    const children = childentries[item.id] || [];
    return {
        loginresult,
        parentpath,
        children,
    };
}
function mapDispatchToProps(dispatch) {
    return {
        onDelete: (item, parentpath) => { dispatch(DELETE_ITEM(item)); dispatch(push(parentpath)); },
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(DeleteButton);
