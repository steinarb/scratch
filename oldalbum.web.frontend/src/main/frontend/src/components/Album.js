import React from 'react';
import { connect } from 'react-redux';
import { NavLink } from 'react-router-dom';
import LoginLogoutButton from './LoginLogoutButton';
import ModifyButton from './ModifyButton';
import AddAlbumButton from './AddAlbumButton';
import AddPictureButton from './AddPictureButton';

function Album(props) {
    const { item, parent, children } = props;

    return (
        <div>
            { parent ? <NavLink to={parent}>Up</NavLink> : <a href="..">Up</a> }
            <br/><LoginLogoutButton/>
            <ModifyButton item={item} />
            <h1>Album: {item.title}</h1>
            { children.map(renderChild) }
            <AddAlbumButton item={item} />
            <AddPictureButton item={item} />
        </div>
    );
}

function mapStateToProps(state, ownProps) {
    const { item } = ownProps;
    const parentEntry = state.albumentries.get(item.parent) || {};
    const parent = parentEntry.path;
    const children = state.childentries.get(item.id) || [];
    return {
        parent,
        children,
    };
}

export default connect(mapStateToProps)(Album);

function renderChild(child, index) {
    if (child.album) {
        return <div key={index}><NavLink to={child.path}>Album: {child.title}</NavLink></div>;
    }

    return <div key={index}><NavLink to={child.path}><img src={child.thumbnailUrl}/></NavLink></div>;
}
