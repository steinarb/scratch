import React from 'react';
import { useSelector } from 'react-redux';
import { NavLink } from 'react-router-dom';
import Locale from './Locale';
import EditModeButton from './EditModeButton';
import CopyLinkButton from './CopyLinkButton';
import ReloadShiroConfigButton from './ReloadShiroConfigButton';
import TogglePasswordProtection from './TogglePasswordProtection';
import DownloadButton from './DownloadButton';
import LoginLogoutButton from './LoginLogoutButton';

export default function PictureNavbar(props) {
    const { className, item, parent, title } = props;
    const text = useSelector(state => state.displayTexts);
    const anchor = 'entry' + item.id.toString();

    return (
        <div className={className}>
            <nav className="navbar navbar-light bg-light">
                <NavLink className="nav-link" to={parent + '#' + anchor}>
                    <div className="container">
                        <div className="column">
                            <span className="row oi oi-chevron-top" title="chevron top" aria-hidden="true"></span>
                            <div className="row">{text.up}</div>
                        </div>
                    </div>
                </NavLink>
                <h1 className="navbar-text">{title}</h1>
                <div className="d-flex flex-row">
                    <DownloadButton className="nav-link float-right" item={item} />
                    <Locale className="form-inline" />
                    <div className="dropdown">
                        <button className="dropdown-toggle" type="button" id="dropdownMenuButton" data-bs-toggle="dropdown" aria-expanded="false">
                            <span className="navbar-toggler-icon"></span>
                        </button>
                        <div className="dropdown-menu dropdown-menu-end" aria-labelledby="dropdownMenuButton">
                            <CopyLinkButton className="dropdown-item" />
                            <ReloadShiroConfigButton className="dropdown-item"/>
                            <TogglePasswordProtection className="dropdown-item" item={item}/>
                            <EditModeButton className="dropdown-item" />
                            <LoginLogoutButton className="dropdown-item" item={item}/>
                        </div>
                    </div>
                </div>
            </nav>
        </div>
    );
}
