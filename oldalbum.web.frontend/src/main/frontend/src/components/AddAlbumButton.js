import React from 'react';
import { connect } from 'react-redux';
import { NavLink } from 'react-router-dom';
import { stringify } from 'qs';

function AddAlbumButton(props) {
    const { loginresult, item } = props;
    if (!loginresult.canModifyAlbum) {
        return null;
    }


    const { id } = item;
    const parent = id; // The new album will have this as a parent
    const addalbum = '/addalbum?' + stringify({ parent });

    return(<NavLink className={(props.className || '') + ' btn btn-primary'} to={addalbum} >Add album</NavLink>);
}

function mapStateToProps(state) {
    return {
        loginresult: state.loginresult,
    };
}

export default connect(mapStateToProps)(AddAlbumButton);
