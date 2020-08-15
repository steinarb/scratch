import React from 'react';
import { connect } from 'react-redux';
import { NavLink } from 'react-router-dom';
import { pictureTitle } from './commonComponentCode';
import LoginLogoutButton from './LoginLogoutButton';
import ModifyButton from './ModifyButton';
import DeleteButton from './DeleteButton';

function Picture(props) {
    const { item, parent } = props;
    const title = pictureTitle(item);

    return (
        <div>
            <nav className="navbar navbar-expand-lg navbar-light bg-light">
                <NavLink to={parent}>
                    <span className="oi oi-chevron-left" title="chevron left" aria-hidden="true"></span>&nbsp;Up
                </NavLink>
                <h1>{title}</h1>
                <button className="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNavDropdown" aria-controls="navbarNavDropdown" aria-expanded="false" aria-label="Toggle navigation">
                    <span className="navbar-toggler-icon"></span>
                </button>
                <div className="collapse navbar-collapse" id="navbarNavDropdown">
                    <div className="navbar-nav">
                        <LoginLogoutButton className="nav-item"/>
                        <ModifyButton className="nav-item nav-link" item={item} />
                        <DeleteButton className="nav-item nav-link" item={item} />
                    </div>
                </div>
            </nav>
            <div>
                <img src={item.imageUrl} />
                {item.description && <div className="alert alert-primary" role="alert">{item.description}</div> }
            </div>
        </div>
    );
}

function mapStateToProps(state, ownProps) {
    const parentEntry = state.albumentries[ownProps.item.parent] || {};
    const parent = parentEntry.path;
    return {
        parent,
    };
}

export default connect(mapStateToProps)(Picture);
