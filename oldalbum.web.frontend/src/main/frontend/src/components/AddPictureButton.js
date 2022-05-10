import React from 'react';
import { connect } from 'react-redux';
import { NavLink } from 'react-router-dom';
import { stringify } from 'qs';

function AddPictureButton(props) {
    const { item } = props;
    const { canModifyAlbum } = props;
    const { id } = item;
    const parent = id; // The new picture will have this as a parent
    const addpicture = '/addpicture?' + stringify({ parent });

    if (!canModifyAlbum) {
        return null;
    }

    return(<NavLink className={(props.className || '') + ' btn btn-primary'} to={addpicture} >Add picture</NavLink>);
}

function mapStateToProps(state) {
    return {
        canModifyAlbum: state.canModifyAlbum,
    };
}

export default connect(mapStateToProps)(AddPictureButton);
