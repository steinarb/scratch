import React from 'react';
import { connect, useDispatch } from 'react-redux';
import LinkIntact from './bootstrap/LinkIntact';
import {
    SET_ALERT,
} from '../reduxactions';

function CopyLinkButton(props) {
    const { alert } = props;
    const dispatch = useDispatch();
    const onCopyUrl = () => {
            copyCurrentUrlToClipboard();
            dispatch(SET_ALERT('URL copied to clipboard'));
    };
    const displayedAlert = alert ? '- ' + alert : '';

    return (
        <span onClick={onCopyUrl} className={(props.className || '') + ' alert alert-primary'} role="alert"><LinkIntact/> Copy link {displayedAlert}</span>
    );
}

function mapStateToProps(state) {
    const alert = state.alert;
    return {
        alert,
    };
}

export default connect(mapStateToProps)(CopyLinkButton);

function copyCurrentUrlToClipboard() {
    const currentLocation = document.createElement('textarea');
    currentLocation.value = window.location.href;
    document.body.appendChild(currentLocation);
    currentLocation.select();
    document.execCommand("copy");
    document.body.removeChild(currentLocation);
}
