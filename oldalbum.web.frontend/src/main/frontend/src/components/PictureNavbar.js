import React from 'react';
import { useSelector } from 'react-redux';
import { NavLink } from 'react-router-dom';
import Locale from './Locale';
import EditModeButton from './EditModeButton';
import CopyLinkButton from './CopyLinkButton';
import DownloadButton from './DownloadButton';
import LoginLogoutButton from './LoginLogoutButton';

export default function PictureNavbar(props) {
    const { className, item, parent, title } = props;
    const text = useSelector(state => state.displayTexts);
    const anchor = 'entry' + item.id.toString();

    return (
        <div className={className}>
            <nav className="navbar navbar-light bg-light">
                <ul className="navbar-nav mr-auto">
                    <li className="nav-item">
                        <NavLink to={parent + '#' + anchor}>
                            <div className="container">
                                <div className="column">
                                    <span className="row oi oi-chevron-top" title="chevron top" aria-hidden="true"></span>
                                    <div className="row">{text.up}</div>
                                </div>
                            </div>
                        </NavLink>
                    </li>
                    <li className="nav-item">
                        <h1>{title}</h1>
                    </li>
                    <li className="nav-item">
                        <DownloadButton className="float-right" item={item} />
                    </li>
                    <li className="nav-item dropdown">
                        <button className="dropdown-toggle" type="button" id="dropdownMenuButton" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                            <span className="navbar-toggler-icon"></span>
                        </button>
                        <div className="dropdown-menu dropdown-menu-right" aria-labelledby="dropdownMenuButton">
                            <Locale className="dropdown-item" />
                            <CopyLinkButton className="dropdown-item" />
                            <EditModeButton className="dropdown-item" />
                            <LoginLogoutButton className="dropdown-item" item={item}/>
                        </div>
                    </li>
                </ul>
            </nav>
        </div>
    );
}
