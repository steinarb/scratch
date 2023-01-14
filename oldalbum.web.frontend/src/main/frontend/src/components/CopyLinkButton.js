import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import LinkIntact from './bootstrap/LinkIntact';
import { SET_ALERT } from '../reduxactions';

export default function CopyLinkButton(props) {
    const text = useSelector(state => state.displayTexts);
    const alert = useSelector(state => state.alert);
    const dispatch = useDispatch();
    const onCopyUrl = () => {
            copyCurrentUrlToClipboard();
            dispatch(SET_ALERT(text.urlcopiedtoclipboard));
    };
    const displayedAlert = alert ? '- ' + alert : '';

    return (
        <span onClick={onCopyUrl} className={(props.className || '') + ' alert alert-primary'} role="alert"><LinkIntact/> {text.copylink} {displayedAlert}</span>
    );
}

function copyCurrentUrlToClipboard() {
    const currentLocation = document.createElement('textarea');
    currentLocation.value = window.location.href;
    document.body.appendChild(currentLocation);
    currentLocation.select();
    document.execCommand("copy");
    document.body.removeChild(currentLocation);
}
