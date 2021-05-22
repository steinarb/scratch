import React from 'react';
import { connect } from 'react-redux';
import { NavLink } from 'react-router-dom';
import { stringify } from 'qs';

function ModifyButton(props) {
    const { webcontext, loginresult, item } = props;
    if (!loginresult.canModifyAlbum) {
        return null;
    }


    const { id } = item;
    const modifyitem = webcontext + (item.album ? '/modifyalbum' : '/modifypicture') + '?' + stringify({ id });

    return(<NavLink className={(props.className || '') + ' btn btn-primary'} to={modifyitem} >Modify</NavLink>);
}

function mapStateToProps(state) {
    const webcontext = state.webcontext || '';
    const loginresult = state.loginresult;
    return {
        webcontext,
        loginresult,
    };
}

export default connect(mapStateToProps)(ModifyButton);
