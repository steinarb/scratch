import React from 'react';
import { useSelector } from 'react-redux';
import { NavLink } from 'react-router-dom';
import Locale from './Locale';
import EditModeButton from './EditModeButton';
import LoginLogoutButton from './LoginLogoutButton';
import CopyLinkButton from './CopyLinkButton';

export default function LoadingOrNotFound() {
    const text = useSelector(state => state.displayTexts);
    const openGraphTitle = (document.head.querySelector('meta[property="og:title"]') || {}).content;
    const titleText = openGraphTitle || text.notfoundTitle;
    const message = openGraphTitle ? text.pageIsLoading : text.resourcenotfound;

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
                <div className="d-flex flex-row">
                    <Locale className="form-inline" />
                    <div className="dropdown">
                        <button className="dropdown-toggle" type="button" id="dropdownMenuButton" data-bs-toggle="dropdown" aria-expanded="false">
                            <span className="navbar-toggler-icon"></span>
                        </button>
                        <ul className="dropdown-menu dropdown-menu-end">
                            <li><CopyLinkButton className="nav-item" /></li>
                            <li><EditModeButton className="nav-item" /></li>
                            <li><LoginLogoutButton className="nav-item" item={{}}/></li>
                        </ul>
                    </div>
                </div>
            </nav>
            <p>{message}</p>
        </div>
    );
}
