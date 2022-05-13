import React from 'react';
import { useSelector } from 'react-redux';
import { NavLink } from 'react-router-dom';
import { stringify } from 'qs';

export default function ModifyButton(props) {
    const { item } = props;
    const webcontext = useSelector(state => state.webcontext) || '';
    const canModifyAlbum = useSelector(state => state.canModifyAlbum);

    if (!canModifyAlbum) {
        return null;
    }


    const { id } = item;
    const modifyitem = webcontext + (item.album ? '/modifyalbum' : '/modifypicture') + '?' + stringify({ id });

    return(<NavLink className={(props.className || '') + ' btn btn-primary'} to={modifyitem} >Modify</NavLink>);
}
