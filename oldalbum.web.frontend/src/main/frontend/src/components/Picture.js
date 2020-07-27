import React from 'react';
import { connect } from 'react-redux';
import { NavLink } from 'react-router-dom';

function Picture(props) {
    const { item, parent } = props;

    return (
        <div>
            { parent ? <NavLink to={parent}>Up</NavLink> : <a href="..">Up</a> }
            <div>
                <img src={item.imageUrl} />
                <p><i>{item.description}</i></p>
            </div>
        </div>
    );
}

function mapStateToProps(state, ownProps) {
    const parentEntry = state.albumentries.get(ownProps.item.parent) || {};
    const parent = parentEntry.path;
    return {
        parent,
    };
}

export default connect(mapStateToProps)(Picture);
