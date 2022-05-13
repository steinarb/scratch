import React from 'react';
import { useSelector } from 'react-redux';
import { NavLink } from 'react-router-dom';
import { stringify } from 'qs';

export default function AddAlbumButton(props) {
    const { item } = props;
    const canModifyAlbum = useSelector(state => state.canModifyAlbum);
    const { id } = item;
    const parent = id; // The new album will have this as a parent
    const addalbum = '/addalbum?' + stringify({ parent });

    if (!canModifyAlbum) {
        return null;
    }

    return(<NavLink className={(props.className || '') + ' btn btn-primary'} to={addalbum} >Add album</NavLink>);
}
