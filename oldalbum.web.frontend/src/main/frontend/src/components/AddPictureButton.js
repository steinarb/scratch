import React from 'react';
import { connect } from 'react-redux';
import { NavLink } from 'react-router-dom';
import { stringify } from 'qs';
import { webcontext } from '../constants';

function AddPictureButton(props) {
    const { loginresult, item } = props;
    if (!loginresult.canModifyAlbum) {
        return null;
    }


    const { id } = item;
    const parent = id; // The new picture will have this as a parent
    const addpicture = webcontext + '/addpicture?' + stringify({ parent });

    return(<NavLink className="{props.styleName} btn btn-primary" to={addpicture} >Add picture</NavLink>);
}

function mapStateToProps(state) {
    const login = state.login || {};
    const loginresult = login.loginresult || { success: false };
    return {
        loginresult,
    };
}

export default connect(mapStateToProps)(AddPictureButton);
