import React from 'react';
import { useSelector } from 'react-redux';
import { NavLink } from 'react-router-dom';
import EditModeButton from './EditModeButton';
import LoginLogoutButton from './LoginLogoutButton';
import CopyLinkButton from './CopyLinkButton';

export default function LoadingOrNotFound() {
    const text = useSelector(state => state.displayTexts);
    const openGraphTitle = (document.head.querySelector('meta[property="og:title"]') || {}).content;
    const titleText = openGraphTitle || text.notfoundTitle;

    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <NavLink to="/">
                    <div className="container">
                        <div className="column">
                            <span className="row oi oi-chevron-top" title="chevron top" aria-hidden="true"></span>
                            <div className="row">{text.up}</div>
                        </div>
                    </div>
                </NavLink>
                <h1>{titleText}</h1>
                <button className="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNavDropdown" aria-controls="navbarNavDropdown" aria-expanded="false" aria-label="Toggle navigation">
                    <span className="navbar-toggler-icon"></span>
                </button>
                <div className="collapse navbar-collapse" id="navbarNavDropdown">
                    <div className="navbar-nav">
                        <CopyLinkButton className="nav-item" />
                        <EditModeButton className="nav-item" />
                        <LoginLogoutButton className="nav-item" item={{}}/>
                    </div>
                </div>
            </nav>
            <p>{text.resourcenotfound}</p>
        </div>
    );
}
