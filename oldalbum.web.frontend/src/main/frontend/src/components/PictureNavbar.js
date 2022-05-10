import React from 'react';
import { NavLink } from 'react-router-dom';
import CopyLinkButton from './CopyLinkButton';
import LoginLogoutButton from './LoginLogoutButton';

export default function PictureNavbar(props) {
    const { className, item, parent, title } = props;

    return (
        <div className={className}>
            <nav className="navbar navbar-light bg-light">
                <NavLink to={parent}>
                    <div className="container">
                        <div className="column">
                            <span className="row oi oi-chevron-top" title="chevron top" aria-hidden="true"></span>
                            <div className="row">Up</div>
                        </div>
                    </div>
                </NavLink>
                <h1>{title}</h1>
                <button className="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNavDropdown" aria-controls="navbarNavDropdown" aria-expanded="false" aria-label="Toggle navigation">
                    <span className="navbar-toggler-icon"></span>
                </button>
                <div className="collapse navbar-collapse" id="navbarNavDropdown">
                    <div className="navbar-nav">
                        <CopyLinkButton className="nav-item" />
                        <LoginLogoutButton className="nav-item" item={item}/>
                    </div>
                </div>
            </nav>
        </div>
    );
}
