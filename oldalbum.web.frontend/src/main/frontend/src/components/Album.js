import React from 'react';
import { connect } from 'react-redux';
import { NavLink } from 'react-router-dom';

function Album(props) {
    const { item, parent, children } = props;

    return (
        <div>
            { parent ? <NavLink to={parent}>Up</NavLink> : <a href="..">Up</a> }
            <h1>Album: {item.title}</h1>
            { children.map(renderChild) }
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
