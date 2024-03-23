import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import {
    CLOSE_WARNING_DIALOG_ENTRY_IS_PASSWORD_PROTECTED,
    REMOVE_PASSWORD_PROTECTION_AND_CLOSE_WARNING_DIALOG,
} from '../reduxactions';

export default function PasswordProtectedWarningDialog() {
    const displayPasswordProtectionWarningDialog = useSelector(state => state.displayPasswordProtectionWarningDialog);
    const text = useSelector(state => state.displayTexts);
    const dispatch = useDispatch();

    if (!displayPasswordProtectionWarningDialog) {
        return null;
    }

    return(
        <dialog className="oldalbum-modal" open>
            <h5 className="modal-title">{text.passwordprotected}</h5>
            <p>{text.sharedlinkispasswordprotected}</p>
            <p>{text.removepassportprotection}</p>
            <p><em>{text.notedoesntaffectparentorsiblings}</em></p>
            <div className="row">
                <button type="button" className="btn btn-secondary col ms-5" onClick={() => dispatch(CLOSE_WARNING_DIALOG_ENTRY_IS_PASSWORD_PROTECTED())}>{text.dontremove}</button>
                <button type="button" className="btn btn-primary col ms-5 me-5" onClick={() => dispatch(REMOVE_PASSWORD_PROTECTION_AND_CLOSE_WARNING_DIALOG())}>{text.remove}</button>
            </div>
        </dialog>
    );
}
