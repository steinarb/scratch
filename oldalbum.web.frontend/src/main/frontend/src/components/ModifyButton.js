import React from 'react';
import { connect } from 'react-redux';
import { NavLink } from 'react-router-dom';
import { stringify } from 'qs';

function ModifyButton(props) {
    const { item } = props;
    const {
        webcontext,
        canModifyAlbum,
    } = props;

    if (!canModifyAlbum) {
        return null;
    }


    const { id } = item;
    const modifyitem = webcontext + (item.album ? '/modifyalbum' : '/modifypicture') + '?' + stringify({ id });

    return(<NavLink className={(props.className || '') + ' btn btn-primary'} to={modifyitem} >Modify</NavLink>);
}

function mapStateToProps(state) {
    const webcontext = state.webcontext || '';
    const canModifyAlbum = state.canModifyAlbum;
    return {
        webcontext,
        canModifyAlbum,
    };
}

export default connect(mapStateToProps)(ModifyButton);
