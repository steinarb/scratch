import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import LinkIntact from './bootstrap/LinkIntact';
import { SHARE_LINK } from '../reduxactions';

export default function CopyLinkButton(props) {
    const text = useSelector(state => state.displayTexts);
    const alert = useSelector(state => state.alert);
    const dispatch = useDispatch();
    const displayedAlert = alert ? '- ' + alert : '';

    return (
        <span onClick={() => dispatch(SHARE_LINK(props.item))} className={props.className || ''}><LinkIntact/> {text.copylink} {displayedAlert}</span>
    );
}
