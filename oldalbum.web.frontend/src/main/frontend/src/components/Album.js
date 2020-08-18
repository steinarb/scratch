import React from 'react';
import { connect } from 'react-redux';
import { NavLink } from 'react-router-dom';
import { pictureTitle } from './commonComponentCode';
import LoginLogoutButton from './LoginLogoutButton';
import ModifyButton from './ModifyButton';
import AddAlbumButton from './AddAlbumButton';
import AddPictureButton from './AddPictureButton';
import DeleteButton from './DeleteButton';
import UpButton from './UpButton';
import DownButton from './DownButton';

function Album(props) {
    const { item, parent, children } = props;

    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                { parent && (
                    <NavLink to={parent}>
                        <span className="oi oi-chevron-left" title="chevron left" aria-hidden="true"></span>&nbsp;Up
                    </NavLink>
                ) }
                <h1>{item.title}</h1>
                <button className="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNavDropdown" aria-controls="navbarNavDropdown" aria-expanded="false" aria-label="Toggle navigation">
                    <span className="navbar-toggler-icon"></span>
                </button>
                <div className="collapse navbar-collapse" id="navbarNavDropdown">
                    <div className="navbar-nav">
                        <LoginLogoutButton className="nav-item" item={item}/>
                    </div>
                </div>
            </nav>
            <div className="btn-group" role="group" aria-label="Modify album">
                <ModifyButton className="mx-1 my-1" item={item} />
                <AddAlbumButton className="mx-1 my-1" item={item} />
                <AddPictureButton className="mx-1 my-1" item={item} />
                <DeleteButton className="mx-1 my-1" item={item} />
            </div>
            {item.description && <div className="alert alert-primary" role="alert">{item.description}</div> }
            { children.sort((a,b) => a.sort - b.sort).map(renderChild) }
        </div>
    );
}

function mapStateToProps(state, ownProps) {
    const { item } = ownProps;
    const parentEntry = state.albumentries[item.parent] || {};
    const parent = parentEntry.path;
    const children = state.childentries[item.id] || [];
    return {
        parent,
        children,
    };
}

export default connect(mapStateToProps)(Album);

function renderChild(child, index) {
    const title = pictureTitle(child);
    if (child.album) {
        return (
            <div key={index} className="btn btn-block btn-primary left-align-cell">
                <NavLink className="btn btn-block btn-primary left-align-cell" to={child.path}>Album: {title}</NavLink>
                <div className="btn-group-vertical">
                    <UpButton item={child} />
                    <DownButton item={child} />
                </div>
            </div>
        );
    }

    return (
        <div key={index} className="btn btn-block btn-primary left-align-cell">
            <NavLink className="btn btn-block btn-primary left-align-cell" to={child.path}>
                <img className="img-thumbnail" src={child.thumbnailUrl}/>&nbsp;{title}
            </NavLink>
            <div className="btn-group-vertical">
                <UpButton item={child} />
                <DownButton item={child} />
            </div>
        </div>
    );
}
