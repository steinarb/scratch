import React from 'react';
import { NavLink } from 'react-router-dom';
import { pictureTitle } from './commonComponentCode';
import UpButton from './UpButton';
import DownButton from './DownButton';

function AlbumEntryOfTypeAlbum(props) {
    const { key, entry, className='' } = props;
    const title = pictureTitle(entry);
    return (
        <div key={key} className={className + ' btn btn-block btn-primary left-align-cell'}>
            <NavLink className="btn btn-block btn-primary left-align-cell" to={entry.path}>Album: {title}</NavLink>
            <div className="btn-group-vertical">
                <UpButton item={entry} />
                <DownButton item={entry} />
            </div>
        </div>
    );
}

export default AlbumEntryOfTypeAlbum;
