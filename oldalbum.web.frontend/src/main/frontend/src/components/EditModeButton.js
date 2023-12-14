import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import {
    TOGGLE_EDIT_MODE_ON,
    TOGGLE_EDIT_MODE_OFF,
} from '../reduxactions';

export default function EditModeButton() {
    const text = useSelector(state => state.displayTexts);
    const canModifyAlbum = useSelector(state => state.canModifyAlbum);
    const loggedIn = useSelector(state => state.loggedIn);
    const editMode = useSelector(state => state.editMode);
    const dispatch = useDispatch();

    if (!loggedIn || !canModifyAlbum) {
        return null;
    }

    if (editMode) {
        return (
            <div className="{props.styleName} alert" role="alert">
                <span onClick={() => dispatch(TOGGLE_EDIT_MODE_OFF())}>{text.switcheditmodeoff}</span>
            </div>
        );
    }

    return(
        <div className="alert" role="alert">
            <span onClick={() => dispatch(TOGGLE_EDIT_MODE_ON())}>{text.switcheditmodeon}</span>
        </div>
    );
}
