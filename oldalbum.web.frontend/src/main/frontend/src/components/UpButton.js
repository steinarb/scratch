import React from 'react';
import { connect } from 'react-redux';
import { MOVE_ALBUMENTRY_UP } from '../reduxactions';

function UpButton(props) {
    const { loginresult, item, onMoveUp } = props;
    // Button doesn't show up if: 1. edit not allowed, 2: this is the first entry in the album
    if (!loginresult.canModifyAlbum || item.sort < 2) {
        return null;
    }

    return(<button className={props.className} type="button" onClick={() => onMoveUp(item)}><span className="oi oi-chevron-top" title="chevron top" aria-hidden="true"></span></button>);
}

function mapStateToProps(state) {
    const login = state.login || {};
    const loginresult = login.loginresult || { success: false };
    return {
        loginresult,
    };
}

function mapDispatchToProps(dispatch) {
    return {
        onMoveUp: (item) => { dispatch(MOVE_ALBUMENTRY_UP(item)); },
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(UpButton);
