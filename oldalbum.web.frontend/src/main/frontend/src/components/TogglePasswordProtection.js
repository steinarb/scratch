import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { TOGGLE_ALBUMENTRY_REQUIRE_LOGIN_REQUEST } from '../reduxactions';

export default function TogglePasswordProtection(props) {
    const item = props.item;
    const text = useSelector(state => state.displayTexts);
    const loggedIn = useSelector(state => state.loggedIn);
    const dispatch = useDispatch();
    const commandText = item.requireLogin ?
          text.removepasswordprotection :
          (item.album ? text.protectalbumwithpassword : text.protectpicturewithpassword);

    if (!loggedIn) {
        return null;
    }

    return (<span className="{props.styleName} alert" role="alert">
                <span className="alert-link" onClick={() => dispatch(TOGGLE_ALBUMENTRY_REQUIRE_LOGIN_REQUEST(item.id))}>{commandText}</span>
            </span>);
}
