import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { RELOAD_SHIRO_CONFIG_REQUEST } from '../reduxactions';

export default function ReloadShiroConfigButton() {
    const text = useSelector(state => state.displayTexts);
    const canModifyAlbum = useSelector(state => state.canModifyAlbum);
    const dispatch = useDispatch();

    if (!canModifyAlbum) {
        return null;
    }

    return (<span className="{props.styleName} alert" role="alert">
                <span className="alert-link" onClick={() => dispatch(RELOAD_SHIRO_CONFIG_REQUEST())}>{text.reloadshirofilter}</span>
            </span>);
}
