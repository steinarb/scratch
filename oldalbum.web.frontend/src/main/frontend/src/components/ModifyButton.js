import React from 'react';
import { connect } from 'react-redux';
import { NavLink } from 'react-router-dom';
import { stringify } from 'qs';
import { LOGOUT_REQUEST } from '../reduxactions';
import { webcontext } from '../constants';

function ModifyButton(props) {
    const { loginresult, item } = props;
    if (!loginresult.canModifyAlbum) {
        return null;
    }


    const { id } = item;
    const modifyitem = webcontext + (item.album ? '/modifyalbum' : '/modifypicture') + '?' + stringify({ id });

    return(<NavLink className={(props.className || '') + ' btn btn-primary'} to={modifyitem} >Modify</NavLink>);
}

function mapStateToProps(state) {
    const login = state.login || {};
    const loginresult = login.loginresult || { success: false };
    return {
        loginresult,
    };
}

export default connect(mapStateToProps)(ModifyButton);
