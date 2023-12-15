import React from 'react';
import { NavLink } from 'react-router-dom';
import Locale from './Locale';
import EditModeButton from './EditModeButton';
import CopyLinkButton from './CopyLinkButton';
import DownloadButton from './DownloadButton';
import LoginLogoutButton from './LoginLogoutButton';

export default function PictureNavbar(props) {
    const { className, item, parent, title } = props;
    const anchor = 'entry' + item.id.toString();

    return (
        <div className={className}>
            <nav className="navbar navbar-light bg-light">
                <NavLink to={parent + '#' + anchor}>
                    <div className="container">
                        <div className="column">
                            <span className="row oi oi-chevron-top" title="chevron top" aria-hidden="true"></span>
                            <div className="row">Up</div>
                        </div>
                    </div>
                </NavLink>
                <h1 className="navbar-title">{title}</h1>
                <div className="d-flex flex-row">
                    <DownloadButton className="" item={item} />
                    <div className="dropdown">
                        <button className="dropdown-toggle" type="button" id="dropdownMenuButton" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                            <span className="navbar-toggler-icon"></span>
                        </button>
                        <div className="dropdown-menu dropdown-menu-right" aria-labelledby="dropdownMenuButton">
                            <Locale className="dropdown-item" />
                            <CopyLinkButton className="dropdown-item" />
                            <EditModeButton className="dropdown-item" />
                            <LoginLogoutButton className="dropdown-item" item={item}/>
                        </div>
                    </div>
                </div>
            </nav>
        </div>
    );
}
