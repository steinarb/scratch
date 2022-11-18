import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import {
    TOGGLE_EDIT_MODE_ON,
    TOGGLE_EDIT_MODE_OFF,
} from '../reduxactions';

export default function EditModeButton() {
    const canModifyAlbum = useSelector(state => state.canModifyAlbum);
    const loggedIn = useSelector(state => state.loggedIn);
    const editMode = useSelector(state => state.editMode);
    const dispatch = useDispatch();

    if (!loggedIn || !canModifyAlbum) {
        return null;
    }

    if (editMode) {
        return (<span className="{props.styleName} alert alert-primary" role="alert">
                    <span className="alert-link" onClick={() => dispatch(TOGGLE_EDIT_MODE_OFF())()}>Switch edit mode off</span>
                </span>);
    }

    return(<span className="alert alert-primary" role="alert">
               <span className="alert-link" onClick={() => dispatch(TOGGLE_EDIT_MODE_ON())()}>Switch edit mode on</span>
           </span>);
}
