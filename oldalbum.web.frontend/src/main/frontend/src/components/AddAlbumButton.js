import React from 'react';
import { useSelector } from 'react-redux';
import { NavLink } from 'react-router-dom';
import { stringify } from 'qs';

export default function AddAlbumButton(props) {
    const { item } = props;
    const text = useSelector(state => state.displayTexts);
    const showEditControls = useSelector(state => state.showEditControls);
    const { id } = item;
    const parent = id; // The new album will have this as a parent
    const addalbum = '/addalbum?' + stringify({ parent });

    if (!showEditControls) {
        return null;
    }

    return(<NavLink className={(props.className || '') + ' btn btn-light'} to={addalbum} >{text.addalbum}</NavLink>);
}
