import React from 'react';
import { NavLink } from 'react-router-dom';

function Thumbnail(props) {
    const { entry, className='' } = props;

    return (
        <div className={className}>
            <NavLink to={entry.path}>
                <img className="img-thumbnail" src={entry.thumbnailUrl}/>
            </NavLink>
        </div>
    );
}

export default Thumbnail;
